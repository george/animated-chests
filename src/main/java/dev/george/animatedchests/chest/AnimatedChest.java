package dev.george.animatedchests.chest;

import dev.george.animatedchests.chest.offset.Offset;
import dev.george.animatedchests.hologram.Hologram;
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

import java.util.*;

@Getter
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

    private final Set<Location> openedChests = new HashSet<>();

    @Setter private Hologram hologram;

    private Player lastPlayerUse;

    public AnimatedChest(Location baseLocation) {
        this.baseLocation = baseLocation;
    }

    public void play(Player player) {
        this.lastPlayerUse = player;

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

        player.teleport(baseLocation);
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

            openChest(chest);
        });
    }

    public void openChest(Chest chest) {
        Location location = chest.getLocation();
        net.minecraft.server.v1_8_R3.World world = ((CraftWorld) location.getWorld()).getHandle();

        BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
        TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(position);

        world.playBlockAction(position, tileChest.w(), 1, 1);
    }
}
