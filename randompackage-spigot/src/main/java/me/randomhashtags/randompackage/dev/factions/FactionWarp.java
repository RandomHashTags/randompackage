package me.randomhashtags.randompackage.dev.factions;

import me.randomhashtags.randompackage.addon.util.Identifiable;
import org.bukkit.Location;

import java.util.UUID;

public interface FactionWarp extends Identifiable {
    UUID getCreator();
    long getCreationTime();
    Location getLocation();
    String getPassword();
}
