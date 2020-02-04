package me.randomhashtags.randompackage.dev;

import com.sun.istack.internal.NotNull;
import me.randomhashtags.randompackage.addon.Stronghold;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UInventory;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.obj.PolyBoundary;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class Strongholds extends RPFeature implements CommandExecutor {
    private static Strongholds instance;
    public static Strongholds getStrongholds() {
        if(instance == null) instance = new Strongholds();
        return instance;
    }

    public YamlConfiguration config;
    private UInventory gui;
    private int captureTask;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        final int l = args.length;
        switch (l) {
            case 1:
                if(args[0].equals("sell")) {
                }
                break;
            default:
                if(player != null) {
                    viewStrongholds(player);
                }
                break;
        }
        if(sender instanceof Player) {
            viewStrongholds((Player) sender);
        }
        return true;
    }

    public String getIdentifier() {
        return "STRONGHOLDS";
    }
    public void load() {
        final long started = System.currentTimeMillis();
        save("strongholds", "_settings.yml");
        if(!otherdata.getBoolean("saved default strongholds")) {
            otherdata.set("saved default strongholds", true);
            saveOtherData();
        }

        config = YamlConfiguration.loadConfiguration(new File(DATA_FOLDER + SEPARATOR + "strongholds", "_settings.yml"));
        gui = new UInventory(null, config.getInt("gui.size"), colorize(config.getString("gui.title")));
        final Inventory gi = gui.getInventory();

        for(File f : new File(DATA_FOLDER + SEPARATOR + "strongholds").listFiles()) {
            if(!f.getName().equals("_settings.yml")) {
                /*final FileStronghold s = new FileStronghold(f);
                gi.setItem(s.getSlot(), s.getItem());*/
            }
        }

        captureTask = SCHEDULER.scheduleSyncRepeatingTask(RANDOM_PACKAGE, () -> {
            for(Stronghold s : getAllStrongholds().values()) {
                final ActiveStronghold a = s.getActiveStronghold();
                if(a != null) {
                    final Collection<Entity> entities = s.getSquareCaptureZone().getEntities();
                }
            }
        }, 20, 20);

        sendConsoleDidLoadFeature(getAll(Feature.STRONGHOLD).size() + " Strongholds", started);
    }
    public void unload() {
        SCHEDULER.cancelTask(captureTask);
        unregister(Feature.STRONGHOLD);
    }

    public void viewStrongholds(@NotNull Player player) {
        if(hasPermission(player, "RandomPackage.stronghold", true)) {
            player.closeInventory();
            final int size = gui.getSize();
            player.openInventory(Bukkit.createInventory(player, size, gui.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(gui.getInventory().getContents());
            for(int i = 0; i < size; i++) {
                item = top.getItem(i);
                if(item != null) {
                    itemMeta = item.getItemMeta();
                }
            }
            player.updateInventory();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        if(event.getView().getTitle().equals(gui.getTitle())) {
            final Player player = (Player) event.getWhoClicked();
            event.setCancelled(true);
            player.updateInventory();
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void blockPlaceEvent(BlockPlaceEvent event) {
        final Location loc = event.getBlockPlaced().getLocation();
        for(Stronghold stronghold : getAllStrongholds().values()) {
            final ActiveStronghold active = stronghold.getActiveStronghold();
            if(active != null && stronghold.getZone().contains(loc)) {
                final List<PolyBoundary> walls = active.getRepairableWalls();
                for(PolyBoundary p : walls) {
                    if(p.contains(loc)) {
                        active.getBlockDurability().put(loc, stronghold.getBlockDurability());
                        break;
                    }
                }
                break;
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void blockBreakEvent(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Location loc = event.getBlock().getLocation();
        for(Stronghold stronghold : getAllStrongholds().values()) {
            final ActiveStronghold active = stronghold.getActiveStronghold();
            if(active != null && stronghold.getZone().contains(loc)) {
                event.setCancelled(true);
                player.updateInventory();
                final HashMap<Location, Integer> durability = active.getBlockDurability();
                final int currentDurability = durability.getOrDefault(loc, -1);
                if(currentDurability != -1) {
                    final int next = currentDurability-1;
                    if(next == 0) {
                        loc.getWorld().getBlockAt(loc).setType(Material.AIR);
                    } else {
                        durability.put(loc, next);
                    }
                }
                break;
            }
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void projectileLaunchEvent(ProjectileLaunchEvent event) {
        if(event.getEntity() instanceof EnderPearl) {
        }
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
    }
}
