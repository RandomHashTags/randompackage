package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.Nameable;
import org.bukkit.Location;

import java.util.List;

public interface DuelArena extends Itemable, Nameable {
    List<Location> getLocations();
}
