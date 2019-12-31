package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;

public class TitanAttributes extends RPFeature implements Listener, CommandExecutor {
    private static TitanAttributes instance;
    public static TitanAttributes getTitanAttributes() {
        if(instance == null) instance = new TitanAttributes();
        return instance;
    }

    public YamlConfiguration config;

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }

    public String getIdentifier() { return "TITAN_ATTRIBUTES"; }
    public void load() {
        final long started = System.currentTimeMillis();
        final String folder = DATA_FOLDER + SEPARATOR + "titan attributes";
        save("titan attributes", "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));

        if(!otherdata.getBoolean("saved default titan attributes")) {
            generateDefaultTitanAttributes();
            otherdata.set("saved default titan attributes", true);
            saveOtherData();
        }

        for(File f : new File(folder).listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                //new FileTitanAttribute(f);
            }
        }

        sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.TITAN_ATTRIBUTE).size() + " Titan Attributes &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        unregister(Feature.TITAN_ATTRIBUTE);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
    }
}
