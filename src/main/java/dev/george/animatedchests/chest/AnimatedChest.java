package dev.george.animatedchests.chest;

import dev.george.animatedchests.AnimatedChests;
import dev.george.animatedchests.chest.offset.Offset;
import dev.george.animatedchests.config.chest.ChestType;
import dev.george.animatedchests.hologram.Hologram;
import dev.george.animatedchests.session.ChestSession;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.TileEntityChest;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

@Getter @Setter
public class AnimatedChest {

    private static final List<Offset> OFFSETS = Arrays.asList(
            new Offset(-1, 2, (byte) 2),
            new Offset(1, 2, (byte) 2),
            new Offset(-1, -2, (byte) 3),
            new Offset(1, -2, (byte) 3),
            new Offset(2, -1, (byte) 4),
            new Offset(2, 1, (byte) 4),
            new Offset(-2, -1, (byte) 5),
            new Offset(-2, 1, (byte) 5)
    );

    private final Location baseLocation;

    private Hologram hologram;
    private ChestSession session;

    public AnimatedChest(Location baseLocation) {
        this.baseLocation = baseLocation;
    }

    public void play(Player player, ChestType chestType) {
        World world = baseLocation.getWorld();
        world.getBlockAt(baseLocation).setType(Material.AIR);

        OFFSETS.forEach(offset -> {
            int offsetX = offset.getOffsetX();
            int offsetZ = offset.getOffsetZ();

            Location chestLocation = baseLocation.clone().add(offsetX, 0, offsetZ);

            Block block = chestLocation.getBlock();

            block.setType(Material.CHEST);
            block.setData(offset.getDirection());
        });

        AnimatedChests.getInstance().getHologramManager().unregisterHologram(hologram);
        AnimatedChests.getInstance().getPlayerDataManager().getData(player).useKey(chestType.getName());

        this.session = new ChestSession(this, player, chestType, player.getLocation());
    }

    public void onClickChest(Block block) {
        OFFSETS.stream().map(offset -> {
            Location location = baseLocation.clone().add(offset.getOffsetX(), 0, offset.getOffsetZ());

            if (!location.equals(block.getLocation())) {
                return null;
            }

            return location;
        }).filter(Objects::nonNull).forEach(location -> {
            Chest chest = (Chest) location.getBlock().getState();

            if (session.getOpenedChests().size() >= session.getChestType().getMaxRewards()) {
                return;
            }

            openChest(chest);
            session.handleClickChest(location);
        });
    }

    public void onComplete() {
        Hologram hologram = new Hologram.HologramBuilder()
                .addLine("&a&lOpen Treasure Chests")
                .build(baseLocation.clone().add(new Vector(0, -0.5, 0)));
        AnimatedChests.getInstance().getHologramManager().registerHologram(hologram);

        OFFSETS.forEach(offset -> {
            Location location = baseLocation.clone().add(offset.getOffsetX(), 0, offset.getOffsetZ());

            location.getWorld().getBlockAt(location).setType(Material.AIR);
        });

        this.hologram = hologram;
        baseLocation.getBlock().setType(Material.CHEST);
    }

    public void openChest(Chest chest) {
        Location location = chest.getLocation();
        net.minecraft.server.v1_8_R3.World world = ((CraftWorld) location.getWorld()).getHandle();

        BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
        TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(position);

        world.playBlockAction(position, tileChest.w(), 1, 1);
    }
}
