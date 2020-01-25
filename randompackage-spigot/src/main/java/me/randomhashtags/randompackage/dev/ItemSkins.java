package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;

public class ItemSkins extends RPFeature {
    private static ItemSkins instance;
    public static ItemSkins getItemSkins() {
        if(instance == null) instance = new ItemSkins();
        return instance;
    }

    public YamlConfiguration config;

    public String getIdentifier() { return "ITEM_SKINS"; }
    public void load() {
        final long started = System.currentTimeMillis();
        save("item skins", "_settings.yml");
        final String folder = DATA_FOLDER + SEPARATOR + "item skins";
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));

        for(File f : new File(folder).listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
            }
        }

        sendConsoleMessage("&6[RandomPackage] &aLoaded Item Skins &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }


    @EventHandler(ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
    }
}
