package dev.george.animatedchests.data.database.impl;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;
import dev.george.animatedchests.data.PlayerData;
import dev.george.animatedchests.data.database.IDatabaseEngine;
import org.bson.Document;
import org.bson.UuidRepresentation;

import java.util.UUID;

public class MongoDatabaseEngine implements IDatabaseEngine {

    private final MongoCollection<Document> users;

    public MongoDatabaseEngine(String mongoUri) {
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .uuidRepresentation(UuidRepresentation.STANDARD).build();

        MongoClient mongoClient = MongoClients.create(clientSettings);
        MongoDatabase chests = mongoClient.getDatabase("chests");

        this.users = chests.getCollection("users");
    }

    @Override
    public PlayerData getData(UUID uuid) {
        Document query = new Document("uuid", uuid);
        Document result = users.find(query).first();

        PlayerData data = new PlayerData();

        if (result == null) {
            users.insertOne(new Document("uuid", uuid).append("keys", new Document()));

            return data;
        }

        result.get("keys", Document.class).forEach((key, value) -> {
            data.addKey(key, (int) value);
        });

        return data;
    }

    @Override
    public void save(PlayerData playerData, UUID uuid) {
        Document query = new Document("uuid", uuid);
        Document keys = new Document();

        playerData.getKeys().forEach(keys::append);

        users.updateOne(query, Updates.set("keys", keys));
    }
}
