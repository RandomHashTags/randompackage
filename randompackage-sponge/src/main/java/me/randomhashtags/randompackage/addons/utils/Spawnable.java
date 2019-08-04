package me.randomhashtags.randompackage.addons.utils;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;

public interface Spawnable {
    boolean canSpawnAt(Player player, Location l);
    boolean canSpawnAt(Location l);
    boolean canSpawnAtOwnedIsland();
    boolean canSpawnAtCoopIsland();
    boolean canSpawnAtVisitingIsland();
}
