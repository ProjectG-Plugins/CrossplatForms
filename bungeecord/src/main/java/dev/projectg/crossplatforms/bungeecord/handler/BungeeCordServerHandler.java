package dev.projectg.crossplatforms.bungeecord.handler;

import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.CommandType;
import dev.projectg.crossplatforms.command.DispatchableCommand;
import dev.projectg.crossplatforms.command.custom.CustomCommandCache;
import dev.projectg.crossplatforms.command.custom.InterceptCommand;
import dev.projectg.crossplatforms.handler.BedrockHandler;
import dev.projectg.crossplatforms.handler.FormPlayer;
import dev.projectg.crossplatforms.handler.ServerHandler;
import dev.projectg.crossplatforms.permission.PermissionDefault;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BungeeCordServerHandler extends CustomCommandCache implements ServerHandler, Listener {

    private static final String OP_GROUP = "op";

    private final ProxyServer server;
    private final PluginManager pluginManager;
    private final BungeeAudiences audiences;
    private final CommandSender console;

    public BungeeCordServerHandler(Plugin plugin, BungeeAudiences audiences) {
        this.server = plugin.getProxy();
        this.pluginManager = server.getPluginManager();
        this.audiences = audiences;
        this.console = server.getConsole();
    }

    private ProxiedPlayer getPlayerOrThrow(UUID uuid) {
        ProxiedPlayer player = server.getPlayer(uuid);
        if (player == null) {
            throw new IllegalArgumentException("Failed to find a player with the following UUID: " + uuid);
        }
        return player;
    }

    @Nullable
    @Override
    public FormPlayer getPlayer(UUID uuid) {
        ProxiedPlayer player = server.getPlayer(uuid);
        return (player == null) ? null : new BungeeCordPlayer(player);
    }

    @Nullable
    @Override
    public FormPlayer getPlayer(String name) {
        ProxiedPlayer player = server.getPlayer(name);
        return (player == null) ? null : new BungeeCordPlayer(player);
    }

    @Override
    public List<FormPlayer> getPlayers() {
        return server.getPlayers().stream().map(BungeeCordPlayer::new).collect(Collectors.toList());
    }

    @NotNull
    @Override
    public Audience asAudience(CommandOrigin origin) {
        return audiences.sender((CommandSender) origin.getHandle());
    }

    @Override
    public boolean isGeyserEnabled() {
        return pluginManager.getPlugin("Geyser-BungeeCord") != null;
    }

    @Override
    public boolean isFloodgateEnabled() {
        return pluginManager.getPlugin("floodgate") != null;
    }

    @Override
    public void registerPermission(String key, @Nullable String description, PermissionDefault def) {
        if (def != PermissionDefault.FALSE) {
            Logger.getLogger().warn("Registering permissions is currently not supported on BungeeCord! Remove permission default settings in configs to stop attempting.");
        }
    }

    @Override
    public void unregisterPermission(String key) {
        Logger.getLogger().debug("Not unregistered permission because registering is currently unsupported on BungeeCord");
    }

    @Override
    public void dispatchCommand(DispatchableCommand command) {
        pluginManager.dispatchCommand(console, command.getCommand());
    }

    @Override
    public void dispatchCommand(UUID uuid, DispatchableCommand command) {
        dispatchCommand(getPlayerOrThrow(uuid), command);
    }

    @Override
    public void dispatchCommands(UUID uuid, List<DispatchableCommand> commands) {
        ProxiedPlayer player = getPlayerOrThrow(uuid);
        commands.forEach(c -> dispatchCommand(player, c));
    }

    public void dispatchCommand(ProxiedPlayer player, DispatchableCommand command) {
        if (command.isPlayer()) {
            if (command.isOp()) {
                player.addGroups(OP_GROUP);
                pluginManager.dispatchCommand(player, command.getCommand());
                player.removeGroups(OP_GROUP);
            } else {
                pluginManager.dispatchCommand(player, command.getCommand());
            }
        } else {
            dispatchCommand(command);
        }
    }

    @EventHandler
    public void onPreProcessCommand(ChatEvent event) {
        Connection connection = event.getSender();
        if (event.isCancelled() || !(connection instanceof ProxiedPlayer) || !event.isCommand() || !event.isProxyCommand()) {
            return;
        }

        String input = event.getMessage().substring(1);
        Logger.getLogger().debug("preprocess command: [" + event.getMessage() + "] -> [" + input + "]");
        // attempt to find an exact match
        InterceptCommand command = findCommand(input);
        if (command != null) {
            ProxiedPlayer player = (ProxiedPlayer) connection;
            BedrockHandler bedrockHandler = CrossplatForms.getInstance().getBedrockHandler();
            if (command.getPlatform().matches(player.getUniqueId(), bedrockHandler)) {
                String permission = command.getPermission();
                if (permission == null || player.hasPermission(permission)) {
                    command.run(
                        new BungeeCordPlayer(player),
                        CrossplatForms.getInstance().getInterfaceManager(),
                        bedrockHandler
                    );

                    if (command.getMethod() == CommandType.INTERCEPT_CANCEL) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
