package me.randomhashtags.randompackage.api.partiallyFinished;

import me.randomhashtags.randompackage.dev.Dungeon;
import me.randomhashtags.randompackage.utils.RPFeature;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;

public class Dungeons extends RPFeature implements CommandExecutor {
    private static Dungeons instance;
    public static Dungeons getDungeons() {
        if(instance == null) instance = new Dungeons();
        return instance;
    }
    public YamlConfiguration config;
    private UInventory gui, master;

    public ItemStack dimensionweb, enchantedobsidian, fuelcell;

    public String getIdentifier() { return "DUNGEONS"; }
    protected RPFeature getFeature() { return getDungeons(); }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        final Player player = sender instanceof Player ? (Player) sender : null;
        if(player != null && args.length == 0 && hasPermission(player, "RandomPackage.dungeons", true)) {
            viewDungeons(player);
        }
        return true;
    }

    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "dungeons.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd, "dungeons.yml"));

        dimensionweb = d(config, "items.dimension web");
        enchantedobsidian = d(config, "items.enchanted obsidian");
        fuelcell = d(config, "items.fuel cell");
        addGivedpCategory(Arrays.asList(dimensionweb, enchantedobsidian, fuelcell), UMaterial.IRON_BARS, "Dungeon Items", "Givedp: Dungeon Items");

        gui = new UInventory(null, config.getInt("gui.size"), ChatColor.translateAlternateColorCodes('&', config.getString("gui.title")));
        master = new UInventory(null, config.getInt("master.size"), ChatColor.translateAlternateColorCodes('&', config.getString("master.title")));
        final ItemStack background = d(config, "gui.background");
        final ItemStack undisDungeon = d(config, "gui.undiscovered.dungeon"), undisKey = d(config, "gui.undiscovered.key");
        final Inventory di = gui.getInventory();
        for(String s : config.getConfigurationSection("gui").getKeys(false)) {
            if(!s.equals("background") && !s.contains("discovered") && config.get("gui." + s + ".slot") != null) {
                final int slot = config.getInt("gui." + s + ".slot");
                final String i = config.getString("gui." + s + ".item").toUpperCase();
                //if(i.startsWith("KEY:")) keys.put(slot, Dungeon.valueOf(config.getString("gui." + s + ".item").split(":")[1]));
                di.setItem(slot, i.equals("{DUNGEON}") ? undisDungeon.clone() : i.equals("{KEY}") || i.startsWith("KEY:") ? undisKey.clone() : d(config, "gui." + s));
            }
        }
        for(int i = 0; i < gui.getSize(); i++) {
            if(di.getItem(i) == null)
                di.setItem(i, background);
        }
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (dungeons != null ? dungeons.size() : 0) + " Dungeons &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        dungeons = null;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(top.getHolder() == player) {
            if(event.getView().getTitle().equals(gui.getTitle())) {
                event.setCancelled(true);
                player.updateInventory();
                final int r = event.getRawSlot();
                final String cl = event.getClick().name();
                final ItemStack c = event.getCurrentItem();
                if(r < 0 || r >= top.getSize() || !cl.contains("LEFT") && !cl.contains("RIGHT") || c == null) return;
            }
        }
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack i = event.getItem();
        if(i != null && i.hasItemMeta()) {
            final Player player = event.getPlayer();
            final Dungeon key = valueOfDungeonFromKey(i), portal = key == null ? valueOfDungeonFromPortal(i) : null;
            if(key != null) {
            } else if(portal != null) {
            } else if(i.isSimilar(dimensionweb) || i.isSimilar(enchantedobsidian) || i.isSimilar(fuelcell)) {
            } else {
                return;
            }
            event.setCancelled(true);
            player.updateInventory();
        }
    }

    public void viewDungeons(Player player) {
        player.closeInventory();
        player.openInventory(Bukkit.createInventory(player, gui.getSize(), gui.getTitle()));
        player.getOpenInventory().getTopInventory().setContents(gui.getInventory().getContents());
        /*for(int i : keys.keySet()) {
            final Dungeon d = keys.get(i);
            if(player.getInventory().containsAtLeast(d.getKey(), 1)) {
            } else {
                player.getOpenInventory().getTopInventory().setItem(i, d.getKeyLocked());
            }
        }*/
        player.updateInventory();
    }
    public void viewMaster(Player player) {
        player.closeInventory();
        player.openInventory(Bukkit.createInventory(player, master.getSize(), master.getTitle()));
        player.updateInventory();
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    private void entityDamageEvent(EntityDamageEvent event) {
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    private void blockBreakEvent(BlockBreakEvent event) {
    }
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void playerTeleportEvent(PlayerTeleportEvent event) {
    }
}
