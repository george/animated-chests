package dev.george.animatedchests;

import dev.george.animatedchests.command.CrateKeyCommand;
import dev.george.animatedchests.config.AnimatedChestConfiguration;
import dev.george.animatedchests.data.database.IDatabaseEngine;
import dev.george.animatedchests.data.manager.PlayerDataManager;
import dev.george.animatedchests.hologram.Hologram;
import dev.george.animatedchests.hologram.manager.HologramManager;
import dev.george.animatedchests.listener.PlayerListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

@Getter
public class AnimatedChests extends JavaPlugin {

    @Getter private static AnimatedChests instance;

    private final HologramManager hologramManager = new HologramManager();
    private final PlayerDataManager playerDataManager = new PlayerDataManager();

    private AnimatedChestConfiguration configuration;
    @Setter private IDatabaseEngine databaseEngine;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        configuration = new AnimatedChestConfiguration(getConfig(), this);

        configuration.getChests().forEach(chest -> {
            Location location = chest.getBaseLocation();

            location.getWorld().getBlockAt(location).setType(Material.CHEST);

            hologramManager.registerHologram(new Hologram.HologramBuilder()
                    .addLine("&a&lOpen Treasure Chests")
                    .build(location.clone().add(new Vector(0, -0.5, 0))
            ));
        });

        getCommand("key").setExecutor(new CrateKeyCommand(this));

        Bukkit.getPluginManager().registerEvents(new PlayerListener(this), this);
        Bukkit.getOnlinePlayers().forEach(playerDataManager::handleJoin);
    }

    @Override
    public void onDisable() {
        hologramManager.removeAllHolograms();

        Bukkit.getOnlinePlayers().forEach(playerDataManager::handleQuit);
    }
}
