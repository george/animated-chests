package dev.george.animatedchests.data.database;

import dev.george.animatedchests.data.PlayerData;

import java.util.UUID;

public interface IDatabaseEngine {

    PlayerData getData(UUID uuid);

    void save(PlayerData playerData, UUID uuid);

}
