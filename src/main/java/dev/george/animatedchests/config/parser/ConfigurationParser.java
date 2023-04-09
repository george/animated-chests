package dev.george.animatedchests.config.parser;

import dev.george.animatedchests.AnimatedChests;
import dev.george.animatedchests.config.chest.ChestType;
import dev.george.animatedchests.data.database.IDatabaseEngine;
import dev.george.animatedchests.data.database.impl.FlatFileDatabase;
import dev.george.animatedchests.data.database.impl.MongoDatabaseEngine;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigurationParser {

    private final FileConfiguration config;

    public ConfigurationParser(FileConfiguration config) {
        this.config = config;
    }

    public Map<String, ChestType> getChestTypes() {
        Map<String, ChestType> chestTypeMap = new HashMap<>();

        config.getConfigurationSection("chest-types").getKeys(false).forEach(key -> {
            ChestType chestType = new ChestType(config.getConfigurationSection(("chest-types." + key)));

            chestTypeMap.put(chestType.getName(), chestType);
        });

        return chestTypeMap;
    }

    public List<Location> getChestLocations() {
        List<Location> locations = new ArrayList<>();

        config.getConfigurationSection("chest-locations").getKeys(false).forEach(key -> {
            locations.add(new LocationParser(config.getConfigurationSection("chest-locations." + key)).getLocation());
        });

        return locations;
    }

    public IDatabaseEngine getDatabaseEngine(AnimatedChests instance) {
        String databaseEngine = config.getString("database.type");

        switch (databaseEngine) {
            case "flatfile":
                return new FlatFileDatabase(instance);
            case "mongodb":
                return new MongoDatabaseEngine(config.getString("database.mongo-uri"));
            default:
                throw new IllegalStateException("Unknown database type set!");
        }
    }
}
