package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.bukkit.Location;

import java.util.List;

public interface DuelArena extends Itemable {
    String getName();
    List<Location> getLocations();
}
