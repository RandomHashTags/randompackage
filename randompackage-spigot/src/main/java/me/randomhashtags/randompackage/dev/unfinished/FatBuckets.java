package me.randomhashtags.randompackage.dev.unfinished;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class FatBuckets extends RPFeature {
    private static FatBuckets instance;
    public static FatBuckets getFatBuckets() {
        if(instance == null) instance = new FatBuckets();
        return instance;
    }

    public YamlConfiguration config;

    public String getIdentifier() { return "FAT_BUCKETS"; }
    protected RPFeature getFeature() { return getFatBuckets(); }

    public void load() {
    }
    public void unload() {
    }

    /*
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void itemDespawnEvent(ItemDespawnEvent event) {
        final ItemStack i = event.getEntity().getItemStack();
        if(i != null && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().hasLore()) {
            final ProgressBucket p = ProgressBucket.valueOf(i);
            if(p != null) ProgressBucket.buckets.remove(p.uuid);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void entityCombustEvent(EntityCombustEvent event) {
        final ItemStack i = event.getEntity() instanceof Item ? ((Item) event.getEntity()).getItemStack() : null;
        if(i != null && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().hasLore()) {
            final ProgressBucket p = ProgressBucket.valueOf(i);
            if(p != null) ProgressBucket.buckets.remove(p.uuid);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerBucketFillEvent(PlayerBucketFillEvent event) {
        final Player player = event.getPlayer();
        final Block clicked = event.getBlockClicked();
        if(progressBuckets.keySet().contains(player)) {
            event.setCancelled(true);
            final ProgressBucket pb = progressBuckets.get(player);
            final CustomBucket b = pb.bucket;
            if(clicked.getType().name().contains("WATER")) {
                clicked.setType(Material.WATER);
            } else {
                if(b.fillableWorlds.contains(clicked.getWorld().getName())) {
                    clicked.setType(Material.AIR);
                    pb.progress += 1;
                    final double p = (((double) pb.progress)/(b.sourcesRequired))*100;
                    player.setItemInHand(b.getPercent((int) p, pb.uuid, true));
                } else {
                    clicked.setType(Material.LAVA);
                    for(String s : b.filledMsg) player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
                }
            }
            player.updateInventory();
            using.remove(player);
            progressBuckets.remove(player);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void playerInteractEvent(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final ItemStack i = event.getItem();
        final CustomBucket c = CustomBucket.valueOfUses(i);
        if(c != null) {
            using.put(player, i);
            usesBuckets.put(player, c);
        }
        final ProgressBucket b = ProgressBucket.valueOf(i);
        if(b != null) {
            using.put(player, i);
            progressBuckets.put(player, b);
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerBucketEmptyEvent(PlayerBucketEmptyEvent event) {
        final Player player = event.getPlayer();
        if(usesBuckets.keySet().contains(player)) {
            event.setCancelled(true);
            final ItemStack i = using.get(player);
            final CustomBucket b = usesBuckets.get(player);
            final Location l = event.getBlockClicked().getLocation();
            final World w = l.getWorld();
            if(b.usableWorlds.contains(w.getName())) {
                player.setItemInHand(b.getUses(getRemainingInt(i.getItemMeta().getDisplayName())-1));
                player.updateInventory();
                usesBuckets.remove(player);
                using.remove(player);
                w.getBlockAt(getPlacedLocation(l, event.getBlockFace())).setType(Material.LAVA);
            }
        }
    }*/
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
