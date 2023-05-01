package dev.george.animatedchests.data.database.impl;

import dev.george.animatedchests.data.PlayerData;
import dev.george.animatedchests.data.database.IDatabaseEngine;
import org.bukkit.configuration.ConfigurationSection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MySQLDatabaseEngine implements IDatabaseEngine {

    private final Connection connection;

    public MySQLDatabaseEngine(ConfigurationSection section) {

        String ip = section.getString("ip");
        int port = section.getInt("port");

        String username = section.getString("username");
        String password = section.getString("password");

        String database = section.getString("database");

        try {
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" + ip + ":" + port + "/" + database, username, password);

            PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `Chest_Users` (" +
                    "uuid CHAR(36)," +
                    "chestType VARCHAR(25)," +
                    "amount INT NOT NULL DEFAULT 0," +
                    "PRIMARY KEY (uuid, chestType)" +
                    ")");

            statement.execute();
        } catch (Exception exc) {
            exc.printStackTrace();

            throw new RuntimeException("Failed to connect to database!");
        }
    }

    @Override
    public PlayerData getData(UUID uuid) {
        PlayerData data = new PlayerData();

        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `Chest_Users` WHERE uuid='" +
                    uuid.toString() + "'");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String chestType = resultSet.getString("chestType");
                int amount = resultSet.getInt("amount");

                data.addKey(chestType, amount);
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return data;
    }

    @Override
    public void save(PlayerData playerData, UUID uuid) {
        try {
            PreparedStatement statement = connection.prepareStatement("DELETE FROM `Chest_Users` WHERE uuid='" +
                    uuid.toString() + "'");
            statement.execute();

            playerData.getKeys().forEach((key, value) -> {
                try {
                    PreparedStatement setStatement = connection.prepareStatement("INSERT INTO `Chest_Users` " +
                            "(uuid, chestType, amount) VALUES (?, ?, ?)");

                    setStatement.setString(1, uuid.toString());
                    setStatement.setString(2, key);
                    setStatement.setInt(3, value);

                    setStatement.execute();
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            });
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }
}
