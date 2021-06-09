package dev.projectg.geyserhub.module.scoreboard;

import dev.projectg.geyserhub.GeyserHubMain;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.List;
import java.util.Objects;


public class ScoreboardManager extends Placeholders {
    public ScoreboardManager() {
    }

    public static void addScoreboard() {

        for (Player all : Bukkit.getOnlinePlayers()) {
            createScoreboard(all);
        }

    }

    public static void createScoreboard(Player player) {
        Scoreboard board = Objects.requireNonNull(Bukkit.getServer().getScoreboardManager()).getNewScoreboard();
        Objective objective = board.registerNewObjective("GeyserHub", "dummy", replaceValues(player, GeyserHubMain.getInstance().getConfig().getString("Scoreboard.Title")));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        List<String> text = GeyserHubMain.getInstance().getConfig().getStringList("Scoreboard.Lines");

        // Scoreboards have a max of 15 lines
        int limit = Math.min(text.size(), 16);

        for (int index = 0; index < limit; index++) {
            String formattedLine = replaceValues(player, text.get(index));
            Score score = objective.getScore(ChatColor.translateAlternateColorCodes('&', formattedLine));
            score.setScore(limit - index);
        }
        player.setScoreboard(board);
    }
}