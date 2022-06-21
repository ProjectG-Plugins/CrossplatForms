package dev.kejona.crossplatforms.spigot.common;

import dev.kejona.crossplatforms.Logger;
import dev.kejona.crossplatforms.accessitem.AccessItem;
import dev.kejona.crossplatforms.accessitem.AccessItemRegistry;
import dev.kejona.crossplatforms.config.ConfigManager;
import dev.kejona.crossplatforms.handler.BedrockHandler;
import dev.kejona.crossplatforms.handler.FormPlayer;
import dev.kejona.crossplatforms.handler.Placeholders;
import dev.kejona.crossplatforms.handler.ServerHandler;
import dev.kejona.crossplatforms.interfacing.Interfacer;
import dev.kejona.crossplatforms.spigot.common.handler.SpigotPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Abstract AccessItemRegistry for spigot platforms. Automatically registers {@link SwapHandItemsListener} if
 * the class is event is available on the current server version.
 */
public abstract class SpigotAccessItemsBase extends AccessItemRegistry implements Listener {

    private final Logger logger = Logger.get();
    protected final Interfacer interfacer;
    protected final BedrockHandler bedrockHandler;
    protected final Placeholders placeholders;

    public SpigotAccessItemsBase(JavaPlugin plugin, ConfigManager configManager,
                                 ServerHandler serverHandler,
                                 Interfacer interfacer,
                                 BedrockHandler bedrockHandler,
                                 Placeholders placeholders) {
        super(configManager, serverHandler);
        this.interfacer = interfacer;
        this.bedrockHandler = bedrockHandler;
        this.placeholders = placeholders;

        if (!Bukkit.getVersion().contains("1.8")) { // Older versions not supported, 1.9 and newer has offhand
            Bukkit.getPluginManager().registerEvents(new SwapHandItemsListener(this), plugin);
        }
    }

    public abstract void setItemId(@Nonnull ItemStack itemStack, @Nonnull String identifier);

    /**
     * Attempt to retrieve the Access Item ID that an ItemStack points to. The Access Item ID may or may not refer
     * to an actual AccessItem
     * @param itemStack The ItemStack to check
     * @return The AccessItem ID if the ItemStack contained the name, null if not.
     */
    @Nullable
    public abstract String getItemId(@Nonnull ItemStack itemStack);

    /**
     * Attempt to retrieve the Access Item that an ItemStack points to
     * @param itemStack The ItemStack to check. If it contains null ItemMeta, this will return null.
     * @return The Access Item if the ItemStack contained the identifier of the Access Item, and the Access Item exists. Will return null if their conditions are false.
     */
    @Nullable
    public AccessItem getItem(@Nonnull ItemStack itemStack) {
        String identifier = getItemId(itemStack);
        if (identifier == null) {
            return null;
        } else {
            return super.getItem(identifier);
        }
    }

    public ItemStack createItemStack(AccessItem accessItem, Player player) {
        FormPlayer formPlayer = new SpigotPlayer(player);
        // todo: material lookups on 1.13 on SpigotLegacy seem to fail on materials with underscores.
        // Have not checked any other versions
        Material material = Material.matchMaterial(accessItem.getMaterial());
        if (material == null) {
            logger.severe(String.format("Failed to get access item from '%s' for access item '%s'", accessItem.getMaterial(), accessItem.getIdentifier()));
            material = Material.COMPASS;
        }

        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            logger.severe(String.format("Itemstack from material %s for access item %s has null item meta!", material, accessItem.getIdentifier()));
            item = new ItemStack(Material.COMPASS);
            meta = Objects.requireNonNull(item.getItemMeta());
        }
        meta.setDisplayName(placeholders.setPlaceholders(formPlayer, accessItem.getDisplayName()));
        meta.setLore(placeholders.setPlaceholders(formPlayer, accessItem.getLore()));
        item.setItemMeta(meta);
        setItemId(item, accessItem.getIdentifier());
        return item;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) { // opening menus through access items
        Action action = event.getAction();
        if (action != Action.PHYSICAL) {
            ItemStack item = event.getItem();
            if (item != null) {
                String id = getItemId(item);
                if (id != null) {
                    // Don't allow using the item to break blocks
                    // If it was a right click, using the access item should be the only behaviour
                    event.setCancelled(true);

                    if (super.isEnabled()) {
                        AccessItem access = super.getItem(id);
                        Player player = event.getPlayer();
                        if (access == null) {
                            // item is no longer registered
                            player.getInventory().remove(item);
                        } else if (player.hasPermission(access.permission(AccessItem.Limit.POSSESS))) {
                            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                                access.trigger(new SpigotPlayer(player));
                            }
                        } else {
                            player.sendMessage("You don't have permission to have that.");
                            player.getInventory().remove(item);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) { // keep the access items in place
        // todo: don't allow duplication for creative players
        ItemStack item = event.getCurrentItem();
        if (item != null) {
            String id = getItemId(item);
            if (id != null) {
                if (super.isEnabled()) {
                    HumanEntity human = event.getWhoClicked();
                    AccessItem access = super.getItem(id);
                    if (access == null) {
                        // restrict items that no longer exist
                        event.setCancelled(true);
                    } else if (!human.hasPermission(access.permission(AccessItem.Limit.POSSESS))) {
                        human.sendMessage("You don't have permission to have that.");
                        human.getInventory().remove(item);
                    } else if (!human.hasPermission(access.permission(AccessItem.Limit.MOVE))) {
                        event.setCancelled(true);
                    }
                } else {
                    // restrict items even if the registry is disabled
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) { // restricting dropping
        ItemStack item = event.getItemDrop().getItemStack();
        String id = getItemId(item);
        if (id != null) {
            AccessItem access = super.getItem(id);
            if (access == null) {
                event.setCancelled(true);
            } else {
                Player player = event.getPlayer();
                if (player.hasPermission(access.permission(AccessItem.Limit.DROP))) {
                    if (!player.hasPermission(access.permission(AccessItem.Limit.PRESERVE))) {
                        event.getItemDrop().remove();
                    }
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) { // restricting dropping
        if (!super.isEnabled()) {
            return;
        }

        Player player = event.getEntity();
        Iterator<ItemStack> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            AccessItem access = getItem(iterator.next());
            if (access != null && !player.hasPermission(access.permission(AccessItem.Limit.PRESERVE))) {
                iterator.remove();
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) { // give the access item when the player joins
        regive(event.getPlayer(), AccessItem::isOnJoin);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) { // give the access item when the player respawns
        regive(event.getPlayer(), AccessItem::isOnRespawn);
    }

    @EventHandler
    public void onPlayerChangeWorld(PlayerChangedWorldEvent event) {
        regive(event.getPlayer(), AccessItem::isOnWorldChange);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null) {
                AccessItem access = getItem(item);
                if (access != null && !access.isPersist()) {
                    player.getInventory().remove(item);
                    logger.debug(String.format("Removing access item %s from %s", access.getIdentifier(), player.getName()));
                }
            }
        }
    }

    private void regive(Player player, Predicate<AccessItem> give) {
        List<String> contained = new ArrayList<>(); // Access items the player already has and that will not be removed

        // Remove any access items that are now longer allowed
        for (ItemStack item : player.getInventory()) {
            if (item != null) {
                AccessItem access = getItem(item);
                if (access != null) {
                    if (player.hasPermission(access.permission(AccessItem.Limit.POSSESS))) {
                        contained.add(access.getIdentifier());
                        logger.debug(String.format("%s is keeping access item %s", player.getName(), access.getIdentifier()));
                    } else {
                        player.getInventory().remove(item);
                        logger.debug(String.format("Removed %s from %s because they don't have permission for it", access.getIdentifier(), player.getName()));
                    }
                }
            }
        }

        // Give any access items that should be given
        boolean changedHand = false; // If we have changed the item the player is holding
        for (AccessItem access : super.getItems().values()) {
            if (give.test(access) && access.getPlatform().matches(player.getUniqueId(), bedrockHandler) && player.hasPermission(access.permission(AccessItem.Limit.EVENT))) {
                if (!contained.contains(access.getIdentifier())) {
                    if (super.setHeldSlot() && !changedHand) {
                        giveAccessItem(new SpigotPlayer(player), access, true);
                        changedHand = true;
                        logger.debug("Set held slot to " + access.getSlot());
                    } else {
                        giveAccessItem(new SpigotPlayer(player), access, false);
                    }

                    logger.debug(String.format("Gave access item %s to %s", access.getIdentifier(), player.getName()));
                } else {
                    logger.debug(String.format("%s has permission for access item %s, but they already have it", player.getName(), access.getIdentifier()));
                }
            }
        }
    }

    @Override
    public boolean giveAccessItem(FormPlayer player, AccessItem accessItem, boolean setHeldSlot) {
        return giveAccessItem(Bukkit.getPlayer(player.getUuid()), accessItem, setHeldSlot);
    }

    /**
     * Gives a player an access item
     * @param player The player to give the access item to
     * @param accessItem The access item to give
     * @param setHeldSlot True if the player's selected item should be forced to the access item
     * @return True if the access item was successfully given. False if the inventory was too full.
     */
    public boolean giveAccessItem(Player player, AccessItem accessItem, boolean setHeldSlot) {
        ItemStack accessItemStack = createItemStack(accessItem, player); // todo update placeholders after the fact. but when?

        int desiredSlot = accessItem.getSlot();
        ItemStack oldItem = player.getInventory().getItem(desiredSlot);
        boolean success = false;
        if (oldItem == null || oldItem.getType() == Material.AIR) {
            // put the item in the desired place
            player.getInventory().setItem(desiredSlot, accessItemStack);
            success = true;
        } else {
            for (int i = 0; i < 9; i++) {
                // Try and find an empty slot

                ItemStack otherItem = player.getInventory().getItem(i);
                if (otherItem == null || otherItem.getType() == Material.AIR) {
                    // slot is empty, move the item that is blocking us to it
                    player.getInventory().setItem(i, oldItem);
                    // put the access item in the slot that is no longer blocked
                    player.getInventory().setItem(desiredSlot, accessItemStack);
                    success = true;
                    break;
                }
            }
            // If the player doesn't have the space in their hotbar then they don't get it
        }

        if (success) {
            if (setHeldSlot) {
                // Set the held item to the slot of the access item
                player.getInventory().setHeldItemSlot(accessItem.getSlot());
            }
            return true;
        } else {
            // todo: send message about no success
            return false;
        }
    }
}
