package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import me.randomhashtags.randompackage.addons.utils.Placeable;
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
