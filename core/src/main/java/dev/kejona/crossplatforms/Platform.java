package dev.kejona.crossplatforms;

import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;

import java.util.UUID;

public enum Platform {
    BEDROCK,
    JAVA,
    ALL;

    /**
     * Checks if a given {@link Platform} matches the player's platform.
     * @param player The player to lookup
     * @param platform The platform to compare to the player
     * @param handler Used to check if the player is Bedrock or not
     * @return True if the player is considered to be in, or match the given {@link Platform}.
     */
    public static boolean matches(UUID player, Platform platform, BedrockHandler handler) {
        if (platform == Platform.ALL) {
            return true;
        }

        boolean isBedrock = handler.isBedrockPlayer(player);
        if (platform == Platform.BEDROCK) {
            return isBedrock;
        } else {
            return !isBedrock;
        }
    }

    public static boolean matches(FormPlayer player, Platform platform, BedrockHandler handler) {
        return matches(player.getUuid(), platform, handler);
    }

    public boolean matches(UUID player, BedrockHandler handler) {
        return matches(player, this, handler);
    }

    public boolean matches(FormPlayer player, BedrockHandler handler) {
        return matches(player.getUuid(), handler);
    }
}
