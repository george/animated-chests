package dev.george.animatedchests.hologram;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Hologram {

    private final List<ArmorStand> armorStands = new ArrayList<>();

    public Hologram(Location location, List<String> lines) {

        for(int i = 0; i < lines.size(); i++) {
            createHologram(location.clone().subtract(new Vector(0, 0.5 * i, 0)), lines.get(i));
        }
    }

    private void createHologram(Location location, String name) {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location.clone()
                        .add(new Vector(0.5, 0, 0.5)), EntityType.ARMOR_STAND);

        armorStand.setCustomName(name);
        armorStand.setCustomNameVisible(true);
        armorStand.setVisible(false);
        armorStand.setGravity(false);

        armorStands.add(armorStand);
    }

    public static class HologramBuilder {

        private final List<String> lines = new ArrayList<>();

        public HologramBuilder addLine(String line) {
            lines.add(ChatColor.translateAlternateColorCodes('&', line));
            return this;
        }

        public Hologram build(Location location) {
            return new Hologram(location, lines);
        }
    }
}
