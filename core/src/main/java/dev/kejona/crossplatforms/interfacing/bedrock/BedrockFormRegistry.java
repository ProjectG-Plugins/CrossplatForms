package dev.kejona.crossplatforms.interfacing.bedrock;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.config.ConfigManager;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.interfacing.Interface;
import dev.kejona.crossplatforms.permission.Permission;
import dev.kejona.crossplatforms.reloadable.Reloadable;
import dev.kejona.crossplatforms.reloadable.ReloadableRegistry;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

@Getter
public class BedrockFormRegistry implements Reloadable {

    /**
     * If bedrock forms are enabled. may be false if disabled in the config or if all forms failed to load.
     */
    private boolean enabled = false;
    private final Map<String, BedrockForm> forms = new HashMap<>();

    private final ConfigManager configManager;
    private final ServerHandler serverHandler;

    public BedrockFormRegistry(ConfigManager configManager, ServerHandler serverHandler) {
        this.configManager = configManager;
        this.serverHandler = serverHandler;
        ReloadableRegistry.register(this);
        load();
    }

    private void load() {
        forms.clear();

        if (!configManager.getConfig(FormConfig.class).isPresent()) {
            enabled = false;
            Logger.get().warn("Form config is not present, not enabling forms.");
            return;
        }

        FormConfig config = configManager.getConfig(FormConfig.class).get();
        enabled = config.isEnable();
        if (enabled) {
            for (String identifier : config.getForms().keySet()) {
                BedrockForm form = config.getForms().get(identifier);
                forms.put(identifier, form);

                form.generatePermissions(config);
                for (Permission entry : form.getPermissions().values()) {
                    serverHandler.registerPermission(entry);
                }
            }
        }
    }

    @Override
    public boolean reload() {

        // Unregister permissions
        if (enabled) {
            for (Interface form : forms.values()) {
                for (Permission permission : form.getPermissions().values()) {
                    serverHandler.unregisterPermission(permission.key());
                }
            }
        }

        load();
        return true;
    }

    /**
     * Get a BedrockForm, based off its name.
     * @param formName The menu name
     * @return the BedrockForm, null if it doesn't exist.
     */
    @Nullable
    public BedrockForm getForm(@Nullable String formName) {
        return forms.get(formName);
    }
}
