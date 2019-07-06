package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.CustomExplosion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FileCustomTNT extends CustomExplosion {
    public static HashMap<Location, FileCustomTNT> placed;
    public static HashMap<UUID, FileCustomTNT> primed;

    private ItemStack item;
    public FileCustomTNT(File f) {
        if(placed == null) {
            placed = new HashMap<>();
            primed = new HashMap<>();
        }
        load(f);
        addExplosion(getIdentifier(), this);
    }
    public String getIdentifier() { return "TNT_" + getYamlName(); }
    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "item");
        return item.clone();
    }
    public List<String> getAttributes() { return yml.getStringList("attributes"); }
    public void didExplode(UUID uuid, List<Block> blockList) {
        primed.remove(uuid);
        final HashMap<Location, FileCustomTNT> p = FileCustomTNT.placed;
        if(p != null) {
            for(Block b : blockList) {
                final Location lo = b.getLocation();
                if(p.containsKey(lo)) {
                    lo.getWorld().getBlockAt(lo).setType(Material.AIR);
                    p.get(lo).ignite(lo).setFuseTicks(10+random.nextInt(21));
                }
            }
        }
    }

    public void place(Location l) {
        final Block b = l.getWorld().getBlockAt(l);
        b.setType(Material.TNT);
        b.getState().update();
        placed.put(l, this);
    }
    public TNTPrimed spawn(Location l) {
        final TNTPrimed t = l.getWorld().spawn(l, TNTPrimed.class);
        primed.put(t.getUniqueId(), this);
        return t;
    }
    public TNTPrimed ignite(Location l) {
        placed.remove(l);
        return spawn(l);
    }
}
