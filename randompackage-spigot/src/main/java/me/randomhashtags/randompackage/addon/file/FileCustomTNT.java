package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomExplosion;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class FileCustomTNT extends RPAddonSpigot implements CustomExplosion {
    public static HashMap<Location, FileCustomTNT> PLACED;
    public static HashMap<UUID, FileCustomTNT> PRIMED;

    private ItemStack item;
    public FileCustomTNT(File f) {
        if(PLACED == null) {
            PLACED = new HashMap<>();
            PRIMED = new HashMap<>();
        }
        load(f);
        register(Feature.CUSTOM_EXPLOSION, this);
    }

    @NotNull
    @Override
    public String getIdentifier() {
        return "TNT_" + getYamlName();
    }

    @Override
    public ItemStack getItem() {
        if(item == null) {
            item = createItemStack(yml, "item");
        }
        return getClone(item);
    }
    @Override
    public List<String> getAttributes() {
        return getStringList(yml, "attributes");
    }
    @Override
    public void didExplode(@NotNull UUID uuid, List<Block> blockList) {
        PRIMED.remove(uuid);
        final HashMap<Location, FileCustomTNT> placed = FileCustomTNT.PLACED;
        if(placed != null) {
            for(Block b : blockList) {
                final Location location = b.getLocation();
                if(placed.containsKey(location)) {
                    location.getWorld().getBlockAt(location).setType(Material.AIR);
                    placed.get(location).ignite(location).setFuseTicks(10+RANDOM.nextInt(21));
                }
            }
        }
    }

    public void place(@NotNull Location l) {
        final Block block = l.getWorld().getBlockAt(l);
        block.setType(Material.TNT);
        block.getState().update();
        PLACED.put(l, this);
    }
    public TNTPrimed spawn(@NotNull Location l) {
        final TNTPrimed tnt = l.getWorld().spawn(l, TNTPrimed.class);
        PRIMED.put(tnt.getUniqueId(), this);
        return tnt;
    }
    public TNTPrimed ignite(@NotNull Location l) {
        PLACED.remove(l);
        return spawn(l);
    }
}
