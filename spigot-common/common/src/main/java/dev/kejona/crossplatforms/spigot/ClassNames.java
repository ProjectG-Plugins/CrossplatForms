package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.utils.ReflectionUtils;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Thanks to Floodgate
 * https://github.com/GeyserMC/Floodgate/blob/master/spigot/src/main/java/org/geysermc/floodgate/util/ClassNames.java
 */
public final class ClassNames {

    /**
     * Includes the v at the front
     */
    public static final String NMS_VERSION;
    private static final String CRAFTBUKKIT_PACKAGE;

    public static final Method PLAYER_GET_PROFILE;
    public static final Field META_SKULL_PROFILE;

    static {
        String bukkitVersion = Bukkit.getBukkitVersion();
        String versionPackage = "unknown";

        try {
            String[] hyphenParts = bukkitVersion.split("-");
            if (hyphenParts.length >= 2) {
                // Main version part is the first element (e.g., 1.21)
                String mainVersion = hyphenParts[0];
                // Release part is the second element (e.g., R0.1, R1, R2)
                String releasePart = hyphenParts[1];

                // Remove 'R' prefix and any leading zeros from the release part
                String normalizedReleasePart = normalizeReleasePart(releasePart);

                // Split main version by dot to get major and minor version
                String[] versionParts = mainVersion.split("\\.");
                if (versionParts.length >= 2) {
                    // Combine major version, minor version, and normalized release part
                    versionPackage = "v" + versionParts[0] + "_" + versionParts[1] + "_" + normalizedReleasePart;
                }
            } else {
                throw new IllegalStateException("Bukkit version string does not contain expected format");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to determine NMS version from Bukkit version string", e);
        }

        Bukkit.getLogger().info(versionPackage);
        NMS_VERSION = versionPackage;

        CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit." + NMS_VERSION;

        Class<?> craftPlayer = ReflectionUtils.requireClass(CRAFTBUKKIT_PACKAGE + ".entity.CraftPlayer");
        PLAYER_GET_PROFILE = ReflectionUtils.requireMethod(craftPlayer, "getProfile");

        Class<?> craftMetaSkull = ReflectionUtils.requireClass(CRAFTBUKKIT_PACKAGE + ".inventory.CraftMetaSkull");
        META_SKULL_PROFILE = ReflectionUtils.requireField(craftMetaSkull, "profile");
    }

    private static String normalizeReleasePart(String releasePart) {
        String normalizedPart = releasePart.replace(".","").replaceFirst("R", "").replaceFirst("^0+(?!$)", "");
        if (normalizedPart.isEmpty()) {
            normalizedPart = "1";
        }
        return "R" + normalizedPart;
    }
}