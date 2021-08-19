package dev.projectg.geyserhub.module.menu.bedrock;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.config.ConfigId;
import dev.projectg.geyserhub.reloadable.Reloadable;
import dev.projectg.geyserhub.reloadable.ReloadableRegistry;
import dev.projectg.geyserhub.SelectorLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BedrockFormRegistry implements Reloadable {

    public static final String DEFAULT = "default";

    /**
     * If bedrock forms are enabled. may be false if disabled in the config or if all forms failed to load.
     */
    private boolean isEnabled;
    private final Map<String, BedrockForm> enabledForms = new HashMap<>();

    public BedrockFormRegistry() {
        ReloadableRegistry.registerReloadable(this);
        isEnabled = load();
    }

    private boolean load() {
        FileConfiguration config = GeyserHubMain.getInstance().getConfigManager().getFileConfiguration(ConfigId.SELECTOR);
        SelectorLogger logger = SelectorLogger.getLogger();

        enabledForms.clear();

        if (config.contains("Bedrock-Selector", true) && config.isConfigurationSection("Bedrock-Selector")) {
            ConfigurationSection selectorSection = config.getConfigurationSection("Bedrock-Selector");
            Objects.requireNonNull(selectorSection);

            if (selectorSection.contains("Enable", true) && selectorSection.isBoolean("Enable")) {
                if (selectorSection.getBoolean("Enable")) {
                    if (selectorSection.contains("Forms", true) && selectorSection.isConfigurationSection("Forms")) {
                        ConfigurationSection forms = selectorSection.getConfigurationSection("Forms");
                        Objects.requireNonNull(forms);

                        boolean noSuccess = true;
                        boolean containsDefault = false;
                        for (String entry : forms.getKeys(false)) {
                            if (!forms.isConfigurationSection(entry)) {
                                logger.warn("Bedrock form with name " + entry + " is being skipped because it is not a configuration section");
                                continue;
                            }
                            ConfigurationSection formInfo = forms.getConfigurationSection(entry);
                            Objects.requireNonNull(formInfo);
                            BedrockForm form = new BedrockForm(formInfo);
                            if (form.isEnabled) {
                                enabledForms.put(entry, form);
                                noSuccess = false;
                            } else {
                                logger.warn("Not adding form for config section: " + entry + " because there was a failure loading it.");
                            }
                            if ("default".equals(entry)) {
                                containsDefault = true;
                            }
                        }

                        if (!containsDefault) {
                            logger.warn("Failed to load a default form! The Server Selector compass will not work and players will not be able to open the default form with \"/ghub\"");
                        }
                        if (noSuccess) {
                            logger.warn("Failed to load ALL bedrock forms, due to configuration error.");
                        } else {
                            logger.info("Valid Bedrock forms are: " + enabledForms.keySet());
                            return true;
                        }
                    }
                } else {
                    logger.debug("Not enabling bedrock forms because it is disabled in the config.");
                }
            } else {
                logger.warn("Not enabling bedrock forms because the Enable value is not present in the config.");
            }
        } else {
            logger.warn("Not enabling bedrock forms because the whole configuration section is not present.");
        }
        return false;
    }

    /**
     * @return True, if Java menus are enabled.
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * @return A copy of the keyset of the current enabled menus
     */
    @Nonnull
    public Set<String> getFormNames() {
        return new HashSet<>(enabledForms.keySet());
    }

    /**
     * Get a Java menu, based off its name.
     * @param menuName The menu name
     * @return the JavaMenu, null if it doesn't exist.
     */
    @Nullable
    public BedrockForm getMenu(@Nonnull String menuName) {
        Objects.requireNonNull(menuName);
        return enabledForms.get(menuName);
    }

    @Override
    public boolean reload() {
        isEnabled = load();
        return true;
    }
}