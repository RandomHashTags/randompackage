package me.randomhashtags.randompackage.addons.utils;

import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public interface Placeable extends Identifiable, Rewardable {
    UMaterial getBlock();
    boolean dropsFromSky();
    UMaterial getFallingBlock();
    List<UMaterial> cannotLandAbove();
    List<UMaterial> cannotLandIn();
    default boolean canLand(Location spawnLocation) {
        final World w = spawnLocation.getWorld();
        final int x = spawnLocation.getBlockX(), y = spawnLocation.getBlockY(), z = spawnLocation.getBlockZ();
        final UMaterial above = UMaterial.match(w.getBlockAt(new Location(w, x, y-1, z)).getType().name()), in = UMaterial.match(w.getBlockAt(spawnLocation).getType().name());
        return !cannotLandAbove().contains(above) && !cannotLandIn().contains(in);
    }
}
