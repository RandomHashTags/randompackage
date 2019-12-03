package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.addon.Disguise;
import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.io.File;

public class Disguises extends RPFeature {
    private static Disguises instance;
    public static Disguises getDisguises() {
        if(instance == null) instance = new Disguises();
        return instance;
    }

    public YamlConfiguration config;

    public String getIdentifier() { return "DISGUISES"; }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return true;
    }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "disguises.yml");
        config = YamlConfiguration.loadConfiguration(new File(dataFolder, "disguises.yml"));
        sendConsoleMessage("&6[RandomPackage] &aLoaded " + (disguises != null ? disguises.size() : 0) + " Disguises &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        disguises = null;
    }

    public void viewOwned(Player player) {
        if(hasPermission(player, "RandomPackage.disguise", true)) {
        }
    }
    public void disguise(Player player, Disguise disguise) {
    }
    public void undisguise(Player player) {
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
    }
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void playerMoveEvent(PlayerMoveEvent event) {
    }
}
