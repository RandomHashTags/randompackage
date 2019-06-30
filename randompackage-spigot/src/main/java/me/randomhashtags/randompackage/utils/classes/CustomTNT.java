package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.utils.NamespacedKey;
import me.randomhashtags.randompackage.utils.abstraction.AbstractCustomExplosion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static me.randomhashtags.randompackage.RandomPackage.getPlugin;

public class CustomTNT extends AbstractCustomExplosion {
    public static HashMap<Location, CustomTNT> placed;
    public static HashMap<UUID, CustomTNT> primed;

    private ItemStack item;
    public CustomTNT(File f) {
        if(placed == null) {
            placed = new HashMap<>();
            primed = new HashMap<>();
        }
        load(f);
        created(new NamespacedKey(getPlugin, "TNT_" + getYamlName()));
    }
    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "item");
        return item.clone();
    }
    public List<String> getAttributes() { return yml.getStringList("attributes"); }
    public void didExplode(UUID uuid, List<Block> blockList) {
        primed.remove(uuid);
        final HashMap<Location, CustomTNT> p = CustomTNT.placed;
        if(p != null) {
            final Random random = api.random;
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
    public static void deleteAll() {
        placed = null;
        primed = null;
    }
}
