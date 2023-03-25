package me.randomhashtags.randompackage.api;

import me.randomhashtags.randompackage.addon.FatBucket;
import me.randomhashtags.randompackage.addon.file.FileFatBucket;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UMaterial;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import me.randomhashtags.randompackage.util.RPItemStack;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum FatBuckets implements RPFeatureSpigot, RPItemStack {
    INSTANCE;

    public YamlConfiguration config;

    @Override
    public @NotNull Feature get_feature() {
        return Feature.FAT_BUCKET;
    }

    @Override
    public void load() {
        if(!OTHER_YML.getBoolean("saved default fat buckets")) {
            generateDefaultFatBuckets();
            OTHER_YML.set("saved default fat buckets", true);
            saveOtherData();
        }
        final List<ItemStack> list = new ArrayList<>();
        for(File f : getFilesInFolder(DATA_FOLDER + SEPARATOR + "fat buckets")) {
            final FileFatBucket ffb = new FileFatBucket(f);
            list.add(ffb.getItem(ffb.getUses()));
        }
        addGivedpCategory(list, UMaterial.LAVA_BUCKET, "Fat Buckets", "Givedp: Fat Buckets");
    }
    @Override
    public void unload() {
    }

    public boolean isFatBucket(@NotNull ItemStack is) {
        return getRPItemStackValue(is, "FatBucketInfo") != null;
    }
    @Nullable
    public HashMap<FatBucket, String> getFatBucketInfo(@NotNull ItemStack is) {
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
        final HashMap<FatBucket, String> fb = getFatBucketInfo(is);
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
        final HashMap<FatBucket, String> bucket = getFatBucketInfo(is);
        if(bucket != null) {
            event.setCancelled(true);
            final Location location = event.getBlockClicked().getLocation();
            final World world = location.getWorld();
            final FatBucket target = (FatBucket) bucket.keySet().toArray()[0];
            if(target.getEnabledWorlds().contains(world.getName())) {
                target.didPlace(is);
                world.getBlockAt(getPlacedLocation(location, event.getBlockFace())).setType(Material.LAVA);
            }
            player.updateInventory();
        }
    }
    private int getDirectionX(@NotNull BlockFace direction) {
        switch (direction) {
            case WEST: return -1;
            case EAST: return 1;
            default: return 0;
        }
    }
    private int getDirectionY(@NotNull BlockFace direction) {
        switch (direction) {
            case DOWN: return -1;
            case UP: return 1;
            default: return 0;
        }
    }
    private int getDirectionZ(@NotNull BlockFace direction) {
        switch (direction) {
            case SOUTH: return 1;
            case NORTH: return -1;
            default: return 0;
        }
    }
    @NotNull
    private Location getPlacedLocation(@NotNull Location l, @NotNull BlockFace b) {
        return new Location(l.getWorld(), l.getBlockX()+getDirectionX(b), l.getBlockY()+getDirectionY(b), l.getBlockZ()+getDirectionZ(b));
    }
}
