package dev.george.animatedchests.hologram.manager;

import dev.george.animatedchests.hologram.Hologram;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;

public class HologramManager {

    private final List<Hologram> holograms = new ArrayList<>();

    public void registerHologram(Hologram hologram) {
        this.holograms.add(hologram);
    }

    public void removeAllHolograms() {
        holograms.forEach(hologram -> hologram.getArmorStands().forEach(Entity::remove));
    }
}
