package dev.george.animatedchests.data.database.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dev.george.animatedchests.AnimatedChests;
import dev.george.animatedchests.data.PlayerData;
import dev.george.animatedchests.data.database.IDatabaseEngine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FlatFileDatabase implements IDatabaseEngine {

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final File userDirectory;

    public FlatFileDatabase(AnimatedChests instance) {
        this.userDirectory = new File(instance.getDataFolder() + File.separator + "data" + File.separator);

        if (!this.userDirectory.isDirectory()) {
            this.userDirectory.mkdirs();
        }
    }

    @Override
    public PlayerData getData(UUID uuid) {
        Path path = Paths.get(userDirectory + File.separator + uuid.toString() + ".json");

        if (!path.toFile().exists()) {
            try {
                path.toFile().createNewFile();
            } catch (IOException exc) {
                System.out.println("An unexpected error occurred while attempting to create player data file for " + uuid + "!");
            }

            return new PlayerData();
        }

        byte[] data;

        try {
            data = Files.readAllBytes(path);
        } catch (IOException exc) {
            return new PlayerData();
        }

        JsonObject object = gson.fromJson(new String(data), JsonObject.class);
        PlayerData playerData = new PlayerData();

        object.entrySet().forEach((entry) -> {
            System.out.println(entry.getKey() + ":" + entry.getValue());
            playerData.addKey(entry.getKey(), entry.getValue().getAsInt());
        });

        return playerData;
    }

    @Override
    public void save(PlayerData playerData, UUID uuid) {
        Path path = Paths.get(userDirectory + File.separator + uuid.toString() + ".json");
        JsonObject object = new JsonObject();

        playerData.getKeys().forEach(object::addProperty);

        try {
            Files.write(path, gson.toJson(object).getBytes());
        } catch (IOException exc) {
            System.out.println("An unexpected error occurred while attempting to save data for " + uuid + "!");
        }
    }
}
