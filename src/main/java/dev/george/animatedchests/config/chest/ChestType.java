package dev.george.animatedchests.config.chest;

import dev.george.animatedchests.config.reward.ChestReward;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ChestType {

    private final List<ChestReward> rewards = new ArrayList<>();
    private final List<String> description = new ArrayList<>();

    private final int slot;

    private final String name;
    private final String displayName;

    public ChestType(ConfigurationSection section) {
        this.name = section.getString("name");
        this.displayName = ChatColor.translateAlternateColorCodes('&', section.getString("displayName"));
        this.description.addAll(section.getStringList("description")
                .stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList())
        );

        this.slot = section.getInt("slot");

        section.getConfigurationSection("rewards").getKeys(false).forEach(key -> {
            this.rewards.add(new ChestReward(section.getConfigurationSection("rewards." + key)));
        });
    }
}
