package me.randomhashtags.randompackage.dev.nearFinished;

import me.randomhashtags.randompackage.dev.FileStronghold;
import me.randomhashtags.randompackage.util.RPFeature;
import me.randomhashtags.randompackage.util.universal.UInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import java.io.File;

public class Strongholds extends RPFeature implements CommandExecutor {
    private static Strongholds instance;
    public static Strongholds getStrongholds() {
        if(instance == null) instance = new Strongholds();
        return instance;
    }

    public YamlConfiguration config;
    private UInventory gui;

    public String getIdentifier() { return "STRONGHOLDS"; }
    protected RPFeature getFeature() { return getStrongholds(); }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(sender instanceof Player) {
            viewStrongholds((Player) sender);
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save("strongholds", "_settings.yml");
        if(!otherdata.getBoolean("saved default strongholds")) {
            final String[] a = new String[] {"FROZEN", "INFERNAL"};
            for(String s : a) save("strongholds", s + ".yml");
            otherdata.set("saved default strongholds", true);
            saveOtherData();
        }

        config = YamlConfiguration.loadConfiguration(new File(rpd + separator + "strongholds", "_settings.yml"));
        gui = new UInventory(null, config.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', config.getString("gui.title")));
        final Inventory gi = gui.getInventory();

        for(File f : new File(rpd + separator + "strongholds").listFiles()) {
            if(!f.getName().equals("_settings.yml")) {
                final FileStronghold s = new FileStronghold(f);
                gi.setItem(s.getSlot(), s.getItem());
            }
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (strongholds != null ? strongholds.size() : 0) + " Strongholds &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        strongholds = null;
    }

    public void viewStrongholds(Player player) {
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
    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void blockPlaceEvent(BlockPlaceEvent event) {
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void blockBreakEvent(BlockBreakEvent event) {
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
