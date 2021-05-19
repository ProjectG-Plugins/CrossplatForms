package dev.projectg.serverselector.command;

import dev.projectg.serverselector.SelectorLogger;
import dev.projectg.serverselector.form.SelectorForm;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public class SelectorCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                SelectorForm.sendForm(player);
            } else {
                player.sendMessage(ChatColor.RED + "Sorry, this is only a Bedrock Edition command!");
            }
        } else if (sender instanceof ConsoleCommandSender) {
            SelectorLogger.getLogger().warn("This command only works in-game!");
        }
        return true;
    }
}