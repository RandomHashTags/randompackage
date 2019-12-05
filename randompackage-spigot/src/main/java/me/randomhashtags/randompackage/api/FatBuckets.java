package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.FatBucket;
import me.randomhashtags.randompackage.dev.Feature;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.RPItemStack;
import me.randomhashtags.randompackage.addon.file.FileFatBucket;
import me.randomhashtags.randompackage.util.universal.UMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FatBuckets extends RPFeature implements RPItemStack {
    private static FatBuckets instance;
    public static FatBuckets getFatBuckets() {
        if(instance == null) instance = new FatBuckets();
        return instance;
    }

    public YamlConfiguration config;

    public String getIdentifier() { return "FAT_BUCKETS"; }
    public void load() {
        final long started = System.currentTimeMillis();
        if(!otherdata.getBoolean("saved default fat buckets")) {
            final String[] a = new String[]{"LAVA"};
            for(String s : a) save("fat buckets", s + ".yml");
            otherdata.set("saved default fat buckets", true);
            saveOtherData();
        }
        final List<ItemStack> buckets = new ArrayList<>();
        for(File f : new File(dataFolder + separator + "fat buckets").listFiles()) {
            final FileFatBucket ffb = new FileFatBucket(f);
            buckets.add(ffb.getItem(ffb.getUses()));
        }
        addGivedpCategory(buckets, UMaterial.LAVA_BUCKET, "Fat Buckets", "Givedp: Fat Buckets");
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.FAT_BUCKET).size() + " Fat Buckets &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        unregister(Feature.FAT_BUCKET);
    }

    public HashMap<FatBucket, String> isFatBucket(ItemStack is) {
        final String info = getRPItemStackValue(is, "FatBucketInfo");
        if(info != null) {
            final HashMap<FatBucket, String> bucket = new HashMap<>();
            bucket.put(getFatBucket(info.split(":")[0]), info);
            return bucket;
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerBucketFillEvent(PlayerBucketFillEvent event) {
        final Player player = event.getPlayer();
        final ItemStack is = player.getItemInHand();
        final HashMap<FatBucket, String> fb = isFatBucket(is);
        if(fb != null) {
            event.setCancelled(true);
            final FatBucket target = (FatBucket) fb.keySet().toArray()[0];
            final Block clicked = event.getBlockClicked();
            if(clicked.getType().name().contains("WATER")) {
                clicked.setType(Material.WATER);
            } else if(target.getFillableInWorlds().contains(clicked.getWorld().getName())) {
                clicked.setType(Material.AIR);
                target.didFill(is);
            } else {
                clicked.setType(Material.LAVA);
                sendStringListMessage(player, target.getOnlyFillableInWorldsMsg(), null);
            }
            player.updateInventory();
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();
        final ItemStack is = player.getItemInHand();
        final HashMap<FatBucket, String> fb = isFatBucket(is);
        if(fb != null) {
            event.setCancelled(true);
            final Location l = event.getBlockClicked().getLocation();
            final World w = l.getWorld();
            final FatBucket target = (FatBucket) fb.keySet().toArray()[0];
            if(target.getEnabledWorlds().contains(w.getName())) {
                target.didPlace(is);
                w.getBlockAt(getPlacedLocation(l, event.getBlockFace())).setType(Material.LAVA);
            }
            player.updateInventory();
        }
    }
    private int getDirectionX(BlockFace b) {
        switch (b) {
            case WEST: return -1;
            case EAST: return 1;
            default: return 0;
        }
    }
    private int getDirectionY(BlockFace b) {
        switch (b) {
            case DOWN: return -1;
            case UP: return 1;
            default: return 0;
        }
    }
    private int getDirectionZ(BlockFace b) {
        switch (b) {
            case SOUTH: return 1;
            case NORTH: return -1;
            default: return 0;
        }
    }
    private Location getPlacedLocation(Location l, BlockFace b) {
        return new Location(l.getWorld(), l.getBlockX()+getDirectionX(b), l.getBlockY()+getDirectionY(b), l.getBlockZ()+getDirectionZ(b));
    }
}
