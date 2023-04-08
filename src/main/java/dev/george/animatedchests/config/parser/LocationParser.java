package dev.george.animatedchests.config.parser;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

@Getter
public class LocationParser {

    private final Location location;

    public LocationParser(ConfigurationSection section) {
        this.location = new Location(
                Bukkit.getWorld(section.getString("world")),
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z")
        );
    }
}
