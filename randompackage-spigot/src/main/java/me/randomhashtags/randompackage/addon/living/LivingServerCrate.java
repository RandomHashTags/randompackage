package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.addon.ServerCrate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;

public final class LivingServerCrate {
    public static HashMap<Location, LivingServerCrate> LIVING;
    private ServerCrate type;
    private Location location;
    public LivingServerCrate(ServerCrate type, Location location) {
        if(LIVING == null) {
            LIVING = new HashMap<>();
        }
        this.type = type;
        this.location = location;
        LIVING.put(location, this);
    }
    public ServerCrate getType() {
        return type;
    }
    public Location getLocation() {
        return location;
    }
    public void delete(boolean drop) {
        final World w = location.getWorld();
        w.getBlockAt(location).setType(Material.AIR);
        if(drop) {
            w.dropItemNaturally(location, type.getItem());
        }
        LIVING.remove(location);
        location = null;
        type = null;
        if(LIVING.isEmpty()) {
            LIVING = null;
        }
    }

    public static void deleteAll(boolean drop) {
        if(LIVING != null) {
            for(LivingServerCrate l : new ArrayList<>(LIVING.values())) {
                l.delete(drop);
            }
            LIVING = null;
        }
    }
}
