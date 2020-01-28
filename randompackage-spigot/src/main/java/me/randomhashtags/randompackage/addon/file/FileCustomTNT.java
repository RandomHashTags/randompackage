package me.randomhashtags.randompackage.addon.file;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.CustomExplosion;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class FileCustomTNT extends RPAddon implements CustomExplosion {
    public static HashMap<Location, FileCustomTNT> placed;
    public static HashMap<UUID, FileCustomTNT> primed;

    private ItemStack item;
    public FileCustomTNT(File f) {
        if(placed == null) {
            placed = new HashMap<>();
            primed = new HashMap<>();
        }
        load(f);
        register(Feature.CUSTOM_EXPLOSION, this);
    }
    public String getIdentifier() {
        return "TNT_" + getYamlName();
    }

    public ItemStack getItem() {
        if(item == null) item = API.d(yml, "item");
        return getClone(item);
    }
    public List<String> getAttributes() {
        return getStringList(yml, "attributes");
    }
    public void didExplode(UUID uuid, List<Block> blockList) {
        primed.remove(uuid);
        final HashMap<Location, FileCustomTNT> p = FileCustomTNT.placed;
        if(p != null) {
            for(Block b : blockList) {
                final Location lo = b.getLocation();
                if(p.containsKey(lo)) {
                    lo.getWorld().getBlockAt(lo).setType(Material.AIR);
                    p.get(lo).ignite(lo).setFuseTicks(10+RANDOM.nextInt(21));
                }
            }
        }
    }

    public void place(@NotNull Location l) {
        final Block b = l.getWorld().getBlockAt(l);
        b.setType(Material.TNT);
        b.getState().update();
        placed.put(l, this);
    }
    public TNTPrimed spawn(@NotNull Location l) {
        final TNTPrimed t = l.getWorld().spawn(l, TNTPrimed.class);
        primed.put(t.getUniqueId(), this);
        return t;
    }
    public TNTPrimed ignite(@NotNull Location l) {
        placed.remove(l);
        return spawn(l);
    }
}
