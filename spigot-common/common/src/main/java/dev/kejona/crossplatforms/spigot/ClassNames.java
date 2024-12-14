package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.utils.ReflectionUtils;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

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

    private static final Map<String, String> NMS_VERSIONS = new HashMap<String, String>() {{
        put("1.21", "v1_21_R1");
        put("1.21.1", "v1_21_R1");
        put("1.21.2", "v1_21_R2");
        put("1.21.3", "v1_21_R2");
        put("1.21.4", "v1_21_R3");
    }};

    static {
        System.out.println(Bukkit.getServer().getBukkitVersion().split("-")[0]);
        String nmsVersion = null;
        try {
            nmsVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            // 如果在這裡發生錯誤，則使用 NMS_VERSIONS 中對應的版本
            nmsVersion = NMS_VERSIONS.get(Bukkit.getServer().getBukkitVersion().split("-")[0]);
        }
        NMS_VERSION = nmsVersion;
        System.out.println(NMS_VERSION);
        CRAFTBUKKIT_PACKAGE = "org.bukkit.craftbukkit." + NMS_VERSION;

        Class<?> craftPlayer = ReflectionUtils.requireClass(CRAFTBUKKIT_PACKAGE + ".entity.CraftPlayer");
        PLAYER_GET_PROFILE = ReflectionUtils.requireMethod(craftPlayer, "getProfile");

        Class<?> craftMetaSkull = ReflectionUtils.requireClass(CRAFTBUKKIT_PACKAGE + ".inventory.CraftMetaSkull");
        META_SKULL_PROFILE = ReflectionUtils.requireField(craftMetaSkull, "profile");
    }
}
