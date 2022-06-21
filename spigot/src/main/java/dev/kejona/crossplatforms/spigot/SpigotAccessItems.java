package dev.kejona.crossplatforms.spigot;

import dev.kejona.crossplatforms.accessitem.AccessItem;
import dev.kejona.crossplatforms.config.ConfigManager;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.Placeholders;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.interfacing.Interfacer;
import dev.kejona.crossplatforms.spigot.common.SpigotAccessItemsBase;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class SpigotAccessItems extends SpigotAccessItemsBase {

    public static final NamespacedKey ACCESS_ITEM_KEY = new NamespacedKey(CrossplatFormsSpigot.getInstance(), AccessItem.STATIC_IDENTIFIER);
    public static final PersistentDataType<String, String> ACCESS_ITEM_KEY_TYPE = PersistentDataType.STRING;

    public SpigotAccessItems(JavaPlugin plugin,
                             ConfigManager configManager,
                             ServerHandler serverHandler,
                             Interfacer interfacer,
                             BedrockHandler bedrockHandler,
                             Placeholders placeholders) {
        super(plugin, configManager, serverHandler, interfacer, bedrockHandler, placeholders);
    }

    @Override
    public void setItemId(@Nonnull ItemStack itemStack, @Nonnull String identifier) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            throw new IllegalArgumentException("ItemStack " + itemStack + " does not have ItemMeta");
        } else {
            meta.getPersistentDataContainer().set(ACCESS_ITEM_KEY, ACCESS_ITEM_KEY_TYPE, identifier);
            itemStack.setItemMeta(meta);
        }
    }

    /**
     * Attempt to retrieve the Access Item ID that an ItemStack points to. The Access Item ID may or may not refer
     * to an actual AccessItem
     * @param itemStack The ItemStack to check
     * @return The AccessItem ID if the ItemStack contained the name, null if not.
     */
    @Nullable
    @Override
    public String getItemId(@Nonnull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) {
            return null;
        } else {
            return meta.getPersistentDataContainer().get(ACCESS_ITEM_KEY, ACCESS_ITEM_KEY_TYPE);
        }
    }

    /**
     * Implemented individually here because PlayerPickupItemEvent is deprecated on newer versions but this event
     * doesn't exist on older versions
     */
    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) { // Stop players without possession permission to pickup items
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack item = event.getItem().getItemStack();
            String id = getItemId(item);
            if (id != null) {
                AccessItem access = super.getItem(id);
                if (access == null) {
                    event.setCancelled(true);
                } else if (!player.hasPermission(access.permission(AccessItem.Limit.POSSESS))) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
