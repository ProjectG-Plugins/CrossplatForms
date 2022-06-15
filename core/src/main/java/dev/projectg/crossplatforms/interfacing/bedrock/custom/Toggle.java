package dev.projectg.crossplatforms.interfacing.bedrock.custom;

import com.google.inject.Inject;
import dev.projectg.crossplatforms.IllegalValueException;
import dev.projectg.crossplatforms.Resolver;
import dev.projectg.crossplatforms.utils.ParseUtils;
import lombok.Getter;
import lombok.ToString;
import org.geysermc.cumulus.component.ToggleComponent;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import javax.annotation.Nonnull;

@ToString(callSuper = true)
@Getter
@ConfigSerializable
@SuppressWarnings("FieldMayBeFinal")
public class Toggle extends CustomComponent {

    public static final String TYPE = "toggle";

    private String defaultValue = "false";

    @Inject
    private Toggle() {
        super();
    }

    @Override
    public ToggleComponent cumulusComponent() throws IllegalValueException {
        return ToggleComponent.of(text, ParseUtils.getBoolean(defaultValue, "default-value"));
    }

    @Override
    public void placeholders(@Nonnull Resolver resolver) {
        super.placeholders(resolver);
        defaultValue = resolver.apply(defaultValue);
    }

    @Override
    public Toggle copy() {
        Toggle toggle = new Toggle();
        toggle.copyBasics(this);
        toggle.defaultValue = this.defaultValue;
        return toggle;
    }

    @Override
    public Toggle withPlaceholders(Resolver resolver) {
        Toggle copy = copy();
        copy.placeholders(resolver);
        return copy;
    }

    @Nonnull
    @Override
    public String resultIfHidden() {
        return defaultValue;
    }

    @Override
    public String type() {
        return TYPE;
    }
}
