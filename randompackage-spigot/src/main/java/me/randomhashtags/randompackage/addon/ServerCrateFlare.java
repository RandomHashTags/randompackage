package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.Placeable;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public interface ServerCrateFlare extends Itemable, Placeable {
    int getSpawnRadius();
    int getSpawnInDelay();
    int getNearbyRadius();
    List<String> getRequestMsg();
    List<String> getNearbySpawnMsg();
    Location spawn(Player player, Location requestLocation);
}
