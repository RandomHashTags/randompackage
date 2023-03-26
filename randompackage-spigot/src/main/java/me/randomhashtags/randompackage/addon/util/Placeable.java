package me.randomhashtags.randompackage.addon.util;

import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Placeable extends Identifiable, Rewardable {
    @NotNull UMaterial getBlock();
    boolean dropsFromSky();
    UMaterial getFallingBlock();
    @NotNull List<UMaterial> cannotLandAbove();
    @NotNull List<UMaterial> cannotLandIn();
    default boolean canLand(@NotNull Location spawnLocation) {
        final World world = spawnLocation.getWorld();
        final int x = spawnLocation.getBlockX(), y = spawnLocation.getBlockY(), z = spawnLocation.getBlockZ();
        final UMaterial above = UMaterial.match(world.getBlockAt(new Location(world, x, y-1, z)).getType().name()), in = UMaterial.match(world.getBlockAt(spawnLocation).getType().name());
        return !cannotLandAbove().contains(above) && !cannotLandIn().contains(in);
    }
}
