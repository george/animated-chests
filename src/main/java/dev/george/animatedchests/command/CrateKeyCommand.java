package dev.george.animatedchests.command;

import dev.george.animatedchests.AnimatedChests;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

@AllArgsConstructor
public class CrateKeyCommand implements CommandExecutor {

    private static final String SYNTAX = "&7(&c&l!&7) Invalid syntax! Try /key (player) (type) (amount)";
    private static final String CANNOT_FIND_CRATE_TYPE = "&7(&c&l!&7) Invalid crate type!";
    private static final String INVALID_AMOUNT = "&7(&c&l!&7) Invalid amount specified!";
    private static final String SUCCESS = "&7(&a&l!&7) Successfully given &e%s %d %s &7crate key(s)!";

    private final AnimatedChests instance;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {
            case 0:
            case 1:
            case 2:
                sendMessage(sender, SYNTAX);
                return false;
            case 3:
                Player player = Bukkit.getPlayer(args[0]);
                String crateType = args[1];

                if (!instance.getConfiguration().getChestTypes().keySet().stream().anyMatch(type ->
                        type.equalsIgnoreCase(crateType))) {
                    sendMessage(sender, CANNOT_FIND_CRATE_TYPE);

                    return false;
                }

                int amount;

                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException exc) {
                    sendMessage(sender, INVALID_AMOUNT);
                    return false;
                }

                instance.getPlayerDataManager().getData(player).addKey(crateType, amount);
                sendMessage(sender, String.format(SUCCESS, player.getName(), amount, crateType));
        }

        return false;
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
}
