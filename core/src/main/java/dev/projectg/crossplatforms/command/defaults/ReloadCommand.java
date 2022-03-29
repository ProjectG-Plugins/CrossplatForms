package dev.projectg.crossplatforms.command.defaults;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import dev.projectg.crossplatforms.CrossplatForms;
import dev.projectg.crossplatforms.Logger;
import dev.projectg.crossplatforms.command.CommandOrigin;
import dev.projectg.crossplatforms.command.FormsCommand;
import dev.projectg.crossplatforms.reloadable.ReloadableRegistry;

public class ReloadCommand extends FormsCommand {

    public static final String NAME = "reload";
    public static final String PERMISSION = PERMISSION_BASE + NAME;

    public ReloadCommand(CrossplatForms crossplatForms) {
        super(crossplatForms);
    }

    @Override
    public void register(CommandManager<CommandOrigin> manager, Command.Builder<CommandOrigin> defaultBuilder) {
        manager.command(defaultBuilder
                .literal(NAME)
                .permission(PERMISSION)
                .handler(context -> {
                    CommandOrigin origin = context.getSender();
                    boolean success = ReloadableRegistry.reloadAll();
                    if (!origin.isConsole()) {
                        // reloadable registry handles console messages
                        if (success) {
                            origin.sendMessage(Logger.Level.INFO, "Successfully reloaded");
                        } else {
                            origin.sendMessage(Logger.Level.SEVERE, "There was an error reloading something! Please check the server console for further information.");
                        }
                    }
                })
                .build());
    }
}
