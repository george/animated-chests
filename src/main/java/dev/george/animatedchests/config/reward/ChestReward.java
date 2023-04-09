package dev.george.animatedchests.config.reward;

import dev.george.animatedchests.config.parser.ItemParser;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

@Getter
public class ChestReward {

    private final String name;

    private final Optional<String> command;
    private final Optional<ItemStack> item;

    public ChestReward(ConfigurationSection section) {
        this.name = section.getString("name");

        if (section.getString("command") != null) {
            this.command = Optional.of(section.getString("command"));
            this.item = Optional.empty();
        } else {
            this.item = Optional.of(new ItemParser(section.getConfigurationSection("item")).build());
            this.command = Optional.empty();
        }
    }

    public void complete(Player player) {
        command.ifPresent(s -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", player.getName())));
        item.ifPresent(item -> player.getInventory().addItem(item));
    }
}
