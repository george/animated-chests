package dev.george.animatedchests.config.parser;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.stream.Collectors;

public class ItemParser {

    private final String displayName;

    private final Material material;

    private final List<String> lore;

    private final int amount;

    public ItemParser(ConfigurationSection section) {
        this.displayName = ChatColor.translateAlternateColorCodes('&', section.getString("displayName"));
        this.lore = section.getStringList("lore").stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());

        this.material = Material.valueOf(section.getString("material").toUpperCase());
        this.amount = section.getInt("amount");
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta meta = itemStack.getItemMeta();

        meta.setDisplayName(displayName);
        meta.setLore(lore);

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
