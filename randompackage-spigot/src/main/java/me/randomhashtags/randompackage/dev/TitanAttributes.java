package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public enum TitanAttributes implements RPFeatureSpigot, CommandExecutor {
    INSTANCE;

    public YamlConfiguration config;
    private ItemStack extractor;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return true;
    }

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        final String folder = DATA_FOLDER + SEPARATOR + "titan attributes";
        save("titan attributes", "_settings.yml");
        config = YamlConfiguration.loadConfiguration(new File(folder, "_settings.yml"));
        extractor = createItemStack(config, "items.extractor");

        if(!OTHER_YML.getBoolean("saved default titan attributes")) {
            generateDefaultTitanAttributes();
            OTHER_YML.set("saved default titan attributes", true);
            saveOtherData();
        }

        for(File f : new File(folder).listFiles()) {
            if(!f.getAbsoluteFile().getName().equals("_settings.yml")) {
                //new FileTitanAttribute(f);
            }
        }

        sendConsoleMessage("&6[RandomPackage] &aLoaded " + getAll(Feature.TITAN_ATTRIBUTE).size() + " Titan Attributes &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
        unregister(Feature.TITAN_ATTRIBUTE);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void inventoryClickEvent(InventoryClickEvent event) {
    }
}
