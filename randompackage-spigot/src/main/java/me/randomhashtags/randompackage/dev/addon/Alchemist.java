package me.randomhashtags.randompackage.dev.addon;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class Alchemist extends RPFeature implements CommandExecutor {
    private static Alchemist instance;
    public static Alchemist getAlchemist() {
        if(instance == null) instance = new Alchemist();
        return instance;
    }

    public String getIdentifier() { return "ALCHEMIST"; }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }

    public YamlConfiguration config;

    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Alchemist &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
    }
    @EventHandler
    private void inventoryCloseEvent(InventoryCloseEvent event) {
    }
}
