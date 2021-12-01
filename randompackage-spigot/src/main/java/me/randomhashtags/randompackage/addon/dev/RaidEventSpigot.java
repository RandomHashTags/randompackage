package me.randomhashtags.randompackage.addon.dev;

import me.randomhashtags.randompackage.addon.dev.RaidEvent;
import org.bukkit.Location;

public interface RaidEventSpigot extends RaidEvent {
    Location getLocation();
}
