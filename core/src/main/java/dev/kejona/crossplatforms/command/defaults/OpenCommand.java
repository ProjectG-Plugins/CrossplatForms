package dev.kejona.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import dev.kejona.crossplatforms.CrossplatForms;
import dev.kejona.crossplatforms.command.CommandOrigin;
import dev.kejona.crossplatforms.command.FormsCommand;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.interfacing.Interface;
import dev.kejona.crossplatforms.interfacing.Interfacer;
import dev.kejona.crossplatforms.interfacing.java.JavaMenuRegistry;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class OpenCommand extends FormsCommand {

    public static final String OPEN_NAME = "open";
    public static final String SEND_NAME = "send";
    public static final String PERMISSION = PERMISSION_BASE + OPEN_NAME;
    public static final String PERMISSION_OTHER = PERMISSION_BASE + SEND_NAME;
    private static final String ARGUMENT = "form|menu";

    private final ServerHandler serverHandler;
    private final BedrockHandler bedrockHandler;
    private final Interfacer interfacer;
    private final JavaMenuRegistry javaRegistry;

    public OpenCommand(CrossplatForms crossplatForms) {
        super(crossplatForms);

        this.serverHandler = crossplatForms.getServerHandler();
        this.bedrockHandler = crossplatForms.getBedrockHandler();
        this.interfacer = crossplatForms.getInterfacer();
        this.javaRegistry = crossplatForms.getInterfacer().getJavaRegistry();
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {

        // Base open command
        manager.command(defaultBuilder
                .literal(OPEN_NAME)
                .permission(origin -> origin.hasPermission(PERMISSION) && origin.isPlayer())
                .argument(StringArgument.<CommandOrigin>newBuilder(ARGUMENT)
                        .withSuggestionsProvider((context, s) -> openSuggestions(context))
                        .build())
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    UUID uuid = origin.getUUID().orElseThrow(NoSuchElementException::new);
                    FormPlayer player = Objects.requireNonNull(serverHandler.getPlayer(uuid));
                    String identifier = context.get(ARGUMENT);
                    Interface ui = interfacer.getInterface(identifier, bedrockHandler.isBedrockPlayer(uuid));

                    if (ui == null) {
                        origin.warn("'" + identifier + "' doesn't exist.");
                        return;
                    }
                    if (origin.hasPermission(ui.permission(Interface.Limit.COMMAND))) {
                        if (origin.hasPermission(ui.permission(Interface.Limit.USE))) {
                            ui.send(player);
                        } else {
                            origin.warn("You don't have permission to use: " + identifier);
                        }
                    } else {
                        origin.warn("You don't have permission to send: " + identifier);
                    }
                }));

        // Send command to make other players open a form or menu
        manager.command(defaultBuilder
                .literal(SEND_NAME)
                .permission(PERMISSION_OTHER)
                .argument(StringArgument.<CommandOrigin>newBuilder("player")
                        .withSuggestionsProvider((context, s) -> serverHandler.getPlayers()
                                .stream()
                                .map(FormPlayer::getName)
                                .collect(Collectors.toList()))
                        .build())
                .argument(StringArgument.<CommandOrigin>newBuilder(ARGUMENT)
                        .withSuggestionsProvider((context, s) -> sendSuggestions(context))
                        .build())
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    String target = context.get("player");
                    FormPlayer targetPlayer = serverHandler.getPlayer(target);
                    if (targetPlayer == null) {
                        origin.warn("The player " + target + " doesn't exist.");
                        return;
                    }
                    String identifier = context.get(ARGUMENT);
                    Interface ui = this.interfacer.getInterface(identifier, bedrockHandler.isBedrockPlayer(targetPlayer.getUuid()));
                    if (ui == null) {
                        origin.warn("'" + identifier + "' doesn't exist.");
                        return;
                    }
                    if (origin.hasPermission(ui.permission(Interface.Limit.COMMAND))) {
                        if (targetPlayer.hasPermission(ui.permission(Interface.Limit.USE))) {
                            ui.send(targetPlayer);
                        } else {
                            origin.warn(target + " doesn't have permission to use: " + identifier);
                        }
                    } else {
                        origin.warn("You don't have permission to send: " + identifier);
                    }
                })
                .build()
        );
    }

    private List<String> openSuggestions(CommandContext<CommandOrigin> context) {
        CommandOrigin origin = context.getSender();
        if (origin.isBedrockPlayer(bedrockHandler)) {
            return Collections.emptyList(); // BE players don't get argument suggestions
        }

        return javaRegistry.getMenus().values().stream()
                .filter(menu -> origin.hasPermission(menu.permission(Interface.Limit.COMMAND)))
                .map(Interface::getIdentifier)
                .collect(Collectors.toList());
    }

    private List<String> sendSuggestions(CommandContext<CommandOrigin> context) {
        CommandOrigin origin = context.getSender();
        if (origin.isBedrockPlayer(bedrockHandler)) {
            return Collections.emptyList();
        }

        String recipient = context.get("player");
        FormPlayer target = serverHandler.getPlayer(recipient);
        if (target == null) {
            return Collections.emptyList();
        }

        return interfacer.getInterfaces(bedrockHandler.isBedrockPlayer(target.getUuid()))
                .stream()
                .filter(ui -> origin.hasPermission(ui.permission(Interface.Limit.COMMAND)) && target.hasPermission(ui.permission(Interface.Limit.USE)))
                .map(Interface::getIdentifier)
                .distinct() // Remove duplicates - forms and menus with the same identifier
                .collect(Collectors.toList());
    }
}
