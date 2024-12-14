package dev.kejona.crossplatforms.spigot.v1_21_R3;

import com.mojang.authlib.properties.Property;
import dev.kejona.crossplatforms.spigot.v1_14_R1.Adapter_v1_14_R1;

public class Adapter_v1_21_R3 extends Adapter_v1_14_R1 {

    @Override
    public String propertyValue(Property property) {
        // Property was changed to a record in the version of authlib that 1.20.2 bumped to
        return property.value();
    }
}
