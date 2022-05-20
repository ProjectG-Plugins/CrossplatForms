package dev.projectg.crossplatforms.action;

import com.google.inject.Inject;
import dev.projectg.crossplatforms.command.DispatchableCommand;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.PlaceholderHandler;
import dev.projectg.crossplatforms.handler.ServerHandler;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandsAction extends SimpleAction<List<DispatchableCommand>> {

    public static final String TYPE = "commands";

    private transient final ServerHandler serverHandler;
    private transient final PlaceholderHandler placeholders;

    public CommandsAction(List<DispatchableCommand> commands, ServerHandler serverHandler, PlaceholderHandler placeholders) {
        super(TYPE, commands);
        this.serverHandler = serverHandler;
        this.placeholders = placeholders;
    }

    @Inject
    private CommandsAction(ServerHandler serverHandler, PlaceholderHandler placeholders) {
        this(Collections.emptyList(), serverHandler, placeholders);
    }

    @Override
    public void affectPlayer(@Nonnull FormPlayer player, @Nonnull Map<String, String> additionalPlaceholders) {
        List<DispatchableCommand> resolved = value().stream()
                .map(command -> command.withCommand(placeholders.setPlaceholders(player, command.getCommand(), additionalPlaceholders)))
                .collect(Collectors.toList());

        serverHandler.dispatchCommands(player.getUuid(), resolved);
    }
}

