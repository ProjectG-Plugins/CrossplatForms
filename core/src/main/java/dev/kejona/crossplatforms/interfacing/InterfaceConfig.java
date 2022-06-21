package dev.kejona.crossplatforms.interfacing;

import dev.kejona.crossplatforms.config.Configuration;
import dev.kejona.crossplatforms.permission.PermissionDefault;
import lombok.Getter;
import lombok.ToString;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.Collections;
import java.util.Map;

@ToString
@Getter
@ConfigSerializable
public abstract class InterfaceConfig extends Configuration {

    protected boolean enable = false;

    protected final Map<Interface.Limit, PermissionDefault> globalPermissionDefaults = Collections.emptyMap();
}
