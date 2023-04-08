package dev.george.animatedchests.data.manager;

import dev.george.animatedchests.data.PlayerData;
import dev.george.animatedchests.data.database.IDatabaseEngine;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {

    private final Map<UUID, PlayerData> playerData = new HashMap<>();

    @Setter private IDatabaseEngine databaseEngine;

    public void handleJoin(Player player) {
        playerData.put(player.getUniqueId(), databaseEngine.getData(player.getUniqueId()));
    }

    public PlayerData getData(Player player) {
        return playerData.get(player.getUniqueId());
    }

    public void handleQuit(Player player) {
        databaseEngine.save(playerData.get(player.getUniqueId()), player.getUniqueId());
    }
}
