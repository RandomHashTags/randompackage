package me.randomhashtags.randompackage.addons.living;

import me.randomhashtags.randompackage.addons.EnvoyCrate;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import me.randomhashtags.randompackage.utils.universal.UVersion;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class LivingEnvoyCrate {
    public static HashMap<Integer, HashMap<Location, LivingEnvoyCrate>> living;
    private static UVersion uv;
    private int envoyID;
    private EnvoyCrate type;
    private Location location;
    public LivingEnvoyCrate(int envoyID, EnvoyCrate type, Location location) {
        if(living == null) {
            living = new HashMap<>();
            uv = UVersion.getUVersion();
        }
        this.envoyID = envoyID;
        this.type = type;
        this.location = location;
        final UMaterial bl = type.getBlock();
        final Block b = location.getWorld().getBlockAt(location);
        b.setType(bl.getMaterial());
        b.getState().setRawData(bl.getData());
        if(!living.containsKey(envoyID)) living.put(envoyID, new HashMap<>());
        living.get(envoyID).put(location, this);
    }
    public int getEnvoyID() { return envoyID; }
    public EnvoyCrate getType() { return type; }
    public Location getLocation() { return location; }
    public void delete(boolean dropItems) {
        location.getChunk().load();
        final HashMap<Location, LivingEnvoyCrate> l = living.get(envoyID);
        l.remove(location);
        location.getBlock().setType(Material.AIR);
        if(dropItems) {
            final World w = location.getWorld();
            final List<ItemStack> items = type.getRandomizedRewards();
            for(ItemStack is : items) {
                if(is != null && !is.getType().equals(Material.AIR)) {
                    w.dropItemNaturally(location, is);
                }
            }
        }
        type = null;
        location = null;
        if(l.isEmpty()) {
            living.remove(envoyID);
        }
        if(living.isEmpty()) {
            living = null;
            uv = null;
        }
    }
    public void shootFirework() {
        final Firework fw = type.getFirework();
        if(fw != null) {
            final World w = location.getWorld();
            uv.spawnFirework(fw, new Location(w, location.getX(), location.getY()+1, location.getZ()));
            w.playEffect(location, Effect.STEP_SOUND, location.getBlock().getType());
        }
    }
    public static LivingEnvoyCrate valueOf(Location l) {
        if(living != null) {
            for(int i : living.keySet()) {
                final HashMap<Location, LivingEnvoyCrate> a = living.get(i);
                if(a.containsKey(l)) return a.get(l);
            }
        }
        return null;
    }
}