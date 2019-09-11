package me.randomhashtags.randompackage.addon.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface Spawnable extends Identifiable {
    boolean canSpawnAt(Player player, Location l);
    boolean canSpawnAt(Location l);
    boolean canSpawnAtOwnedIsland();
    boolean canSpawnAtCoopIsland();
    boolean canSpawnAtVisitingIsland();
}