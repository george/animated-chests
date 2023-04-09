package dev.george.animatedchests.session;

import dev.george.animatedchests.AnimatedChests;
import dev.george.animatedchests.chest.AnimatedChest;
import dev.george.animatedchests.config.chest.ChestType;
import dev.george.animatedchests.config.reward.ChestReward;
import dev.george.animatedchests.hologram.Hologram;
import dev.george.animatedchests.hologram.manager.HologramManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Getter
public class ChestSession {

    private final List<ChestReward> rewards = new ArrayList<>();

    private final Set<Location> openedChests = new HashSet<>();
    private final Set<Hologram> holograms = new HashSet<>();

    private final AnimatedChest chest;
    private final ChestType chestType;

    private final Player player;
    private final Location previousLocation;

    public ChestSession(AnimatedChest chest, Player player, ChestType chestType, Location previousLocation) {
        this.chest = chest;
        this.chestType = chestType;

        this.player = player;
        this.previousLocation = previousLocation;

        player.teleport(chest.getBaseLocation().clone().add(new Vector(0.5, 0, 0.5)));
    }

    public void handleClickChest(Location clickedLocation) {
        if (openedChests.contains(clickedLocation)) {
            return;
        }

        for (int i = 0; i < 8; i++) {
            clickedLocation.getWorld().playEffect(clickedLocation.clone().add(new Vector(Math.random(), 0.2, Math.random())),
                    Effect.HEART, 5);
        }

        List<ChestReward> allRewards = chestType.getRewards();

        ChestReward reward = allRewards.get(ThreadLocalRandom.current().nextInt(allRewards.size()));
        Hologram hologram = new Hologram.HologramBuilder()
                .addLine(reward.getName())
                .build(clickedLocation);
        AnimatedChests.getInstance().getHologramManager().registerHologram(hologram);

        reward.complete(player);

        holograms.add(hologram);
        rewards.add(reward);
        openedChests.add(clickedLocation);

        if (openedChests.size() == chestType.getMaxRewards()) {
            Bukkit.getScheduler().runTaskLater(AnimatedChests.getInstance(), this::complete, 60L);
        }
    }

    public void complete() {
        player.teleport(previousLocation);
        chest.onComplete();

        HologramManager hologramManager = AnimatedChests.getInstance().getHologramManager();
        holograms.forEach(hologramManager::unregisterHologram);
    }
}
