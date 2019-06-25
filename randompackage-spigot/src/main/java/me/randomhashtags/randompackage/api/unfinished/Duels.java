package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import java.io.File;

public class Duels extends RandomPackageAPI implements Listener, CommandExecutor {

    private static Duels instance;
    public static final Duels getDuels() {
        if(instance == null) instance = new Duels();
        return instance;
    }

    public boolean isEnabled;
    public YamlConfiguration config;

    private UInventory type, godset;

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) return true;
        final Player player = (Player) sender;
        final int l = args.length;
        if(l == 0) {
            viewTypes(player);
        } else {
            final String a = args[0];
            if(a.equals("godset")) {
                viewGodset(player);
            } else {

            }
        }
        return true;
    }
    public void enable() {
        final long started = System.currentTimeMillis();
        if(isEnabled) return;
        save(null, "duels.yml");
        pluginmanager.registerEvents(this, randompackage);
        isEnabled = true;

        config = YamlConfiguration.loadConfiguration(new File(rpd, "duels.yml"));

        type = new UInventory(null, config.getInt("type.size"), ChatColor.translateAlternateColorCodes('&', config.getString("type.title")));
        godset = new UInventory(null, config.getInt("godset.size"), ChatColor.translateAlternateColorCodes('&', config.getString("godset.title")));

        sendConsoleMessage("&6[RandomPackage] &aLoaded Duels &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void disable() {
        if(!isEnabled) return;
        isEnabled = false;
        config = null;
        type = null;
        godset = null;
        HandlerList.unregisterAll(this);
    }

    public void viewTypes(Player player) {
        if(hasPermission(player, "RandomPackage.duels.view", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(player, type.getSize(), type.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(type.getInventory().getContents());

            player.updateInventory();
        }
    }
    public void viewGodset(Player player) {
        if(hasPermission(player, "RandomPackage.duels.view.godset", true)) {
            player.closeInventory();
            player.openInventory(Bukkit.createInventory(player, godset.getSize(), godset.getTitle()));
            final Inventory top = player.getOpenInventory().getTopInventory();
            top.setContents(godset.getInventory().getContents());

            player.updateInventory();
        }
    }
}
