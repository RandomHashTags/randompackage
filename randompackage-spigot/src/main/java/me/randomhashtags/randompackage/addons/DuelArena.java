package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.bukkit.Location;

import java.util.List;

public abstract class DuelArena extends Itemable {
    public abstract String getName();
    public abstract List<Location> getLocations();
}
