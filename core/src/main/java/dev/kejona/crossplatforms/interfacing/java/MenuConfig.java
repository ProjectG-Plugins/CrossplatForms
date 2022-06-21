package dev.kejona.crossplatforms.interfacing.java;

import dev.kejona.crossplatforms.interfacing.InterfaceConfig;
import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.Map;

@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class MenuConfig extends InterfaceConfig {

    public static final int VERSION = 1;
    public static final int MINIMUM_VERSION = 1;

    private Map<String, JavaMenu> menus = Collections.emptyMap();
}
