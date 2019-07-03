package me.randomhashtags.randompackage.addons.active;

import me.randomhashtags.randompackage.addons.usingfile.FileServerCrate;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;

public class LivingServerCrate {
    public static HashMap<Location, LivingServerCrate> living;
    private FileServerCrate type;
    private Location location;
    public LivingServerCrate(FileServerCrate type, Location location) {
        if(living == null) {
            living = new HashMap<>();
        }
        this.type = type;
        this.location = location;
        living.put(location, this);
    }
    public FileServerCrate getType() { return type; }
    public Location getLocation() { return location; }
    public void delete(boolean drop) {
        final World w = location.getWorld();
        w.getBlockAt(location).setType(Material.AIR);
        if(drop) w.dropItemNaturally(location, type.getItem());
        living.remove(location);
        location = null;
        type = null;
        if(living.isEmpty()) {
            living = null;
        }
    }

    public static void deleteAll(boolean drop) {
        if(living != null) {
            for(LivingServerCrate l : new ArrayList<>(living.values())) {
                l.delete(drop);
            }
            living = null;
        }
    }
}
