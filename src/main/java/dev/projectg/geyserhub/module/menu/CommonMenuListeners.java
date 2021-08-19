package dev.projectg.geyserhub.module.menu;

import dev.projectg.geyserhub.GeyserHubMain;
import dev.projectg.geyserhub.config.ConfigId;
import dev.projectg.geyserhub.module.menu.bedrock.BedrockForm;
import dev.projectg.geyserhub.module.menu.bedrock.BedrockFormRegistry;
import dev.projectg.geyserhub.module.menu.java.JavaMenu;
import dev.projectg.geyserhub.module.menu.java.JavaMenuRegistry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.Objects;

public class CommonMenuListeners implements Listener {

    private final BedrockFormRegistry bedrockFormRegistry;
    private final JavaMenuRegistry javaMenuRegistry;

    public CommonMenuListeners(BedrockFormRegistry bedrockFormRegistry, JavaMenuRegistry javaMenuRegistry) {
        this.bedrockFormRegistry = bedrockFormRegistry;
        this.javaMenuRegistry = javaMenuRegistry;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) { // open the menu through the access item
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().isSimilar(AccessItem.getItem())) {
            if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                    if (bedrockFormRegistry.isEnabled()) {
                        if (bedrockFormRegistry.getFormNames().contains(BedrockFormRegistry.DEFAULT)) {
                            BedrockForm form = Objects.requireNonNull(bedrockFormRegistry.getMenu(BedrockFormRegistry.DEFAULT));
                            form.sendForm(FloodgateApi.getInstance().getPlayer(player.getUniqueId()));
                        } else {
                            player.sendMessage("[GeyserHub]" + ChatColor.RED + " Sorry, this item can't be used because a default form hasn't been specified!");
                        }
                    } else {
                        player.sendMessage("[GeyserHub]" + ChatColor.RED + " Sorry, Bedrock forms are not enabled!");
                    }
                } else {
                    if (javaMenuRegistry.isEnabled()) {
                        if (javaMenuRegistry.getMenuNames().contains(JavaMenuRegistry.DEFAULT)) {
                            JavaMenu menu = Objects.requireNonNull(javaMenuRegistry.getMenu(JavaMenuRegistry.DEFAULT));
                            menu.sendMenu(player);
                        } else {
                            player.sendMessage("[GeyserHub]" + ChatColor.RED + " Sorry, this item can't be used because a default menu hasn't been specified!");
                        }
                    } else {
                        player.sendMessage("[GeyserHub]" + ChatColor.RED + " Sorry, Java menus are not enabled!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) { // keep the access item in place
        // todo: don't allow duplication for creative players
        FileConfiguration config = GeyserHubMain.getInstance().getConfigManager().getFileConfiguration(ConfigId.SELECTOR);
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (!config.getBoolean("Selector-Item.Allow-Move") && event.getCurrentItem().isSimilar(AccessItem.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) { // dont let the access item be dropped
        FileConfiguration config = GeyserHubMain.getInstance().getConfigManager().getFileConfiguration(ConfigId.SELECTOR);
        if (event.getItemDrop().getItemStack().isSimilar(AccessItem.getItem())) {
            if (!config.getBoolean("Selector-Item.Allow-Drop")) {
                event.setCancelled(true);
            } else if (config.getBoolean("Selector-Item.Destroy-Dropped")) {
                event.getItemDrop().remove();
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) { // give the access item when the player joins
        FileConfiguration config = GeyserHubMain.getInstance().getConfigManager().getFileConfiguration(ConfigId.SELECTOR);
        event.getPlayer().getInventory().setHeldItemSlot(GeyserHubMain.getInstance().getConfig().getInt("Selector-Item.Slot"));
        if (config.getBoolean("Selector-Item.Join")) {
            Player player = event.getPlayer();
            ItemStack accessItem = AccessItem.getItem();
            if (player.getInventory().contains(accessItem)) {
                return;
            }

            int desiredSlot = config.getInt("Selector-Item.Slot");
            ItemStack oldItem = player.getInventory().getItem(desiredSlot);
            if (oldItem == null || oldItem.getType() == Material.AIR) {
                player.getInventory().setItem(desiredSlot, accessItem);
            } else {
                for (int i = 0; i < 10; i++) {
                    if (player.getInventory().getItem(i) == null || Objects.requireNonNull(player.getInventory().getItem(i)).getType() == Material.AIR) {
                        player.getInventory().setItem(i, oldItem);
                        player.getInventory().setItem(desiredSlot, accessItem);
                        break;
                    }
                }
                // If the player doesn't have the space in their hotbar then they don't get it
            }
        }
    }
}