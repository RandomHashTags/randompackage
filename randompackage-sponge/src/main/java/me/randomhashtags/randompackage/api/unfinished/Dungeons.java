package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.utils.classes.dungeons.Dungeon;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class Dungeons extends RandomPackageAPI implements CommandExecutor {

    private static Dungeons instance;
    public static Dungeons getDungeons() {
        if(instance == null) instance = new Dungeons();
        return instance;
    }

    public boolean isEnabled = false;
    public YamlConfiguration config;
    private UInventory dungeons, master;
    private ItemStack background;

    public ItemStack dimensionweb, enchantedobsidian, fuelcell, holywhitescroll, soulanvil, soulpearl;

    public CommandResult execute(CommandSource src, CommandContext args) {
        if(src instanceof Player && args.length == 0 && hasPermission(src, "RandomPackage.dungeons", true)) {
            viewDungeons((Player) src);
        }
        return CommandResult.success();
    }

    public void enable() {
        if(isEnabled) return;
        addCommand(this, "RandomPackage.dungeons", "Dungeons!", "dungeon", "dungeons");
        save(null, "dungeons.yml");
        eventmanager.registerListeners(randompackage, this);
        isEnabled = true;
        config = YamlConfiguration.loadConfiguration(new File(rpd, "dungeons.yml"));

        dimensionweb = d(config, "items.dimension web");
        enchantedobsidian = d(config, "items.enchanted obsidian");
        fuelcell = d(config, "items.fuel cell");
        holywhitescroll = d(config, "items.holy white scroll");
        soulanvil = d(config, "items.soul anvil");
        soulpearl = d(config, "items.soul pearl");
        addGivedpCategory(Arrays.asList(dimensionweb, enchantedobsidian, fuelcell, holywhitescroll, soulanvil, soulpearl), UMaterial.IRON_BARS, "Dungeon Items", "Givedp: Dungeon Items");

        dungeons = new UInventory(null, config.getInt("gui.size"), translateColorCodes(config.getString("gui.title")));
        master = new UInventory(null, config.getInt("master.size"), translateColorCodes(config.getString("master.title")));
        background = d(config, "gui.background");
        final ItemStack undisDungeon = d(config, "gui.undiscovered.dungeon"), undisKey = d(config, "gui.undiscovered.key");
        final Inventory di = dungeons.getInventory();
        for(String s : config.getConfigurationSection("gui").getKeys(false)) {
            if(!s.equals("background") && !s.contains("discovered") && config.get("gui." + s + ".slot") != null) {
                final int slot = config.getInt("gui." + s + ".slot");
                final String i = config.getString("gui." + s + ".item").toUpperCase();
                //if(i.startsWith("KEY:")) keys.put(slot, Dungeon.valueOf(config.getString("gui." + s + ".item").split(":")[1]));
                di.setItem(slot, i.equals("{DUNGEON}") ? undisDungeon.copy() : i.equals("{KEY}") || i.startsWith("KEY:") ? undisKey.copy() : d(config, "gui." + s));
            }
        }
        for(int i = 0; i < dungeons.getSize(); i++)
            if(di.getItem(i) == null)
                di.setItem(i, background);
        final HashMap<String, Dungeon> d = Dungeon.dungeons;
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (d != null ? d.size() : 0) + " dungeons");
    }
    public void disable() {
        if(!isEnabled) return;
        isEnabled = false;
        config = null;
        dungeons = null;
        master = null;
        background = null;
        Dungeon.deleteAll();
        eventmanager.unregisterListeners(this);
    }

    @Listener
    private void inventoryClickEvent(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Inventory top = player.getOpenInventory().getTopInventory();
        if(!event.isCancelled() && top.getHolder() == player) {
            if(top.getTitle().equals(dungeons.getTitle())) {
                event.setCancelled(true);
                player.updateInventory();
                final int r = event.getRawSlot();
                final String cl = event.getClick().name();
                final ItemStack c = event.getCurrentItem();
                if(r < 0 || r >= top.getSize() || !cl.contains("LEFT") && !cl.contains("RIGHT") || c == null) return;
            }
        }
    }

    @Listener
    private void playerInteractEvent(PlayerInteractEvent event) {
        final ItemStack i = event.getItem();
        if(i != null && i.hasItemMeta()) {
            final ItemMeta im = i.getItemMeta();
            final Material t = i.getType();
            final byte d = i.getData().getData();
            final String o = im.equals(dimensionweb.getItemMeta()) && t.equals(dimensionweb.getType()) && d == dimensionweb.getData().getData() ? "d"
                            : im.equals(enchantedobsidian.getItemMeta()) && t.equals(enchantedobsidian.getType()) && d == enchantedobsidian.getData().getData() ? "e"
                            : im.equals(fuelcell.getItemMeta()) && t.equals(fuelcell.getType()) && d == fuelcell.getData().getData() ? "f"
                            : im.equals(holywhitescroll.getItemMeta()) && t.equals(holywhitescroll.getType()) && d == holywhitescroll.getData().getData() ? "h"
                            : im.equals(soulanvil.getItemMeta()) && t.equals(soulanvil.getType()) && d == soulanvil.getData().getData() ? "sa"
                            : im.equals(soulpearl.getItemMeta()) && t.equals(soulpearl.getType()) && d == soulpearl.getData().getData() ? "sp"
                            : null;
            if(o != null) {
                final Player player = event.getPlayer();
                event.setCancelled(true);
                player.updateInventory();
                if(o.equals("h")) return;
            }
        }
    }

    public void viewDungeons(Player player) {
        player.closeInventory();
        player.openInventory(Bukkit.createInventory(player, dungeons.getSize(), dungeons.getTitle()));
        player.getOpenInventory().getTopInventory().setContents(dungeons.getInventory().getContents());
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
}
