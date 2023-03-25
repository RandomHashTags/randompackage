package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.addon.EnvoyCrate;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.universal.UVersionableSpigot;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class LivingEnvoyCrate implements UVersionableSpigot {
    public static HashMap<Integer, HashMap<Location, LivingEnvoyCrate>> LIVING;
    private final int envoyID;
    private EnvoyCrate type;
    private Location location;
    public LivingEnvoyCrate(int envoyID, EnvoyCrate type, Location location) {
        if(LIVING == null) {
            LIVING = new HashMap<>();
        }
        this.envoyID = envoyID;
        this.type = type;
        this.location = location;
        final UMaterial blockMaterial = type.getBlock();
        final Block block = location.getWorld().getBlockAt(location);
        block.setType(blockMaterial.getMaterial());
        block.getState().setRawData(blockMaterial.getData());
        LIVING.putIfAbsent(envoyID, new HashMap<>());
        LIVING.get(envoyID).put(location, this);
    }
    public int getEnvoyID() {
        return envoyID;
    }
    public EnvoyCrate getType() {
        return type;
    }
    public Location getLocation() {
        return location;
    }
    public void delete(boolean dropItems) {
        location.getChunk().load();
        final HashMap<Location, LivingEnvoyCrate> l = LIVING.get(envoyID);
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
            LIVING.remove(envoyID);
        }
        if(LIVING.isEmpty()) {
            LIVING = null;
        }
    }
    public void shoot_firework() {
        final Firework firework = type.getFirework();
        if(firework != null) {
            final World world = location.getWorld();
            spawnFirework(firework, new Location(world, location.getX(), location.getY()+1, location.getZ()));
            world.playEffect(location, Effect.STEP_SOUND, location.getBlock().getType());
        }
    }
    @Nullable
    public static LivingEnvoyCrate valueOf(Location location) {
        if(LIVING != null) {
            for(Map.Entry<Integer, HashMap<Location, LivingEnvoyCrate>> entry : LIVING.entrySet()) {
                final int envoyID = entry.getKey();
                final HashMap<Location, LivingEnvoyCrate> living = entry.getValue();
                if(living.containsKey(location)) {
                    return living.get(location);
                }
            }
        }
        return null;
    }
}