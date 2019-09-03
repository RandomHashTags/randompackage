package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.utils.RPFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;

public class Disguises extends RPFeature {
    private static Disguises instance;
    public static Disguises getDisguises() {
        if(instance == null) instance = new Disguises();
        return instance;
    }

    public YamlConfiguration config;

    public String getIdentifier() { return "DISGUISES"; }
    protected RPFeature getFeature() { return getDisguises(); }
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        return true;
    }
    public void load() {
        final long started = System.currentTimeMillis();
        save(null, "disguises.yml");
        config = YamlConfiguration.loadConfiguration(new File(rpd, "disguises.yml"));
        sendConsoleMessage("&6[RandomPackage] &aLoaded Disguises &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }

    public void viewOwned(Player player) {
        if(hasPermission(player, "RandomPackage.disguise", true)) {
        }
    }
    public void disguise(Player player) {
    }
    public void undisguise(Player player) {
    }

    @EventHandler
    private void playerInteractEvent(PlayerInteractEvent event) {
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
    }
}
