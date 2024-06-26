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
            String[] parts = bukkitVersion.split("-")[0].split("\\.");
            if (parts.length >= 2) {
                versionPackage = "v" + parts[0] + "_" + parts[1] + "_R1";
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to determine NMS version", e);
        }

        if (versionPackage.equals("unknown")) {
            throw new IllegalStateException("Unable to determine NMS version from Bukkit version string");
        }

        NMS_VERSION = versionPackage;
        CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit." + NMS_VERSION;
        Class<?> craftPlayer = ReflectionUtils.requireClass(CRAFTBUKKIT_PACKAGE + ".entity.CraftPlayer");
        PLAYER_GET_PROFILE = ReflectionUtils.requireMethod(craftPlayer, "getProfile");

        Class<?> craftMetaSkull = ReflectionUtils.requireClass(CRAFTBUKKIT_PACKAGE + ".inventory.CraftMetaSkull");
        META_SKULL_PROFILE = ReflectionUtils.requireField(craftMetaSkull, "profile");
    }
}
