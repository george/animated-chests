package dev.george.animatedchests.config;

import dev.george.animatedchests.AnimatedChests;
import dev.george.animatedchests.chest.AnimatedChest;
import dev.george.animatedchests.config.chest.ChestType;
import dev.george.animatedchests.config.parser.ConfigurationParser;
import dev.george.animatedchests.data.database.IDatabaseEngine;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class AnimatedChestConfiguration {

    private final Map<String, ChestType> chestTypes = new HashMap<>();

    private final List<AnimatedChest> chests = new ArrayList<>();

    public AnimatedChestConfiguration(FileConfiguration config, AnimatedChests instance) {
        ConfigurationParser parser = new ConfigurationParser(config);

        chestTypes.putAll(parser.getChestTypes());
        chests.addAll(parser.getChestLocations()
                .stream()
                .map(AnimatedChest::new)
                .collect(Collectors.toList())
        );

        IDatabaseEngine engine = parser.getDatabaseEngine(instance);

        instance.setDatabaseEngine(engine);
        instance.getPlayerDataManager().setDatabaseEngine(engine);
    }
}
