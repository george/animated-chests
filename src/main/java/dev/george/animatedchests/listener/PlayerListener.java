package dev.george.animatedchests.listener;

import dev.george.animatedchests.AnimatedChests;
import dev.george.animatedchests.chest.AnimatedChest;
import dev.george.animatedchests.config.chest.ChestType;
import dev.george.animatedchests.data.PlayerData;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
public class PlayerListener implements Listener {

    private final String inventoryName = ChatColor.translateAlternateColorCodes('&', "&c&lOpen Chests");
    private final Map<Player, AnimatedChest> lastClickedChests = new HashMap<>();

    private final AnimatedChests animatedChests;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        animatedChests.getPlayerDataManager().handleJoin(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        animatedChests.getPlayerDataManager().handleQuit(player);
        animatedChests.getConfiguration().getChests().stream()
                .filter(chest -> chest.getSession() != null && player.equals(chest.getSession().getPlayer()))
                .forEach(chest -> {
                    chest.getSession().complete();
                    chest.setSession(null);
                });
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getClickedBlock() == null || !event.getClickedBlock().getType().equals(Material.CHEST)) {
            return;
        }

        Location interactedLocation = event.getClickedBlock().getLocation();

        animatedChests.getConfiguration().getChests().stream()
                .filter(chest -> chest.getSession() != null && player.equals(chest.getSession().getPlayer()))
                .forEach(chest -> {
                    event.setCancelled(true);

                    chest.onClickChest(event.getClickedBlock());
                });

        animatedChests.getConfiguration().getChests().stream()
                .filter(chest -> chest.getBaseLocation().equals(interactedLocation))
                .forEach(chest -> {
                    PlayerData data = animatedChests.getPlayerDataManager().getData(player);
                    Inventory inventory = Bukkit.createInventory(null, 6 * 9, inventoryName);

                    animatedChests.getConfiguration().getChestTypes().values().forEach(chestType -> {
                        ItemStack itemStack = new ItemStack(Material.CHEST, 1);
                        ItemMeta meta = itemStack.getItemMeta();

                        meta.setDisplayName(chestType.getDisplayName());
                        meta.setLore(chestType.getDescription()
                                .stream()
                                .map(line -> line.replace("%quantity%",
                                        Integer.toString(data.getKeyCount(chestType.getName()))))
                                .collect(Collectors.toList())
                        );

                        itemStack.setItemMeta(meta);

                        inventory.setItem(chestType.getSlot(), itemStack);
                    });

                    lastClickedChests.put(player, chest);
                    event.setCancelled(true);
                    player.openInventory(inventory);
                });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();

        if (inventory.getName().equalsIgnoreCase(inventoryName)) {
            event.setCancelled(true);

            int slot = event.getSlot();
            animatedChests.getConfiguration().getChestTypes().values()
                    .stream()
                    .filter(type -> type.getSlot() == slot)
                    .findFirst()
                    .ifPresent((type) -> {
                        lastClickedChests.get(player).play(player, type);
                    });
        }
    }
}
