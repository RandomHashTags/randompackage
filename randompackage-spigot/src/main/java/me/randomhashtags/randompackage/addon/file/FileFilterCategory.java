package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.FilterCategory;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.universal.UInventory;
import org.bukkit.inventory.Inventory;

import java.io.File;

public class FileFilterCategory extends RPAddon implements FilterCategory {
    private UInventory gui;
    public FileFilterCategory(File f) {
        load(f);
        register(Feature.FILTER_CATEGORY, this);
    }
    public String getIdentifier() { return getYamlName(); }

    public String getTitle() {
        return colorize(yml.getString("title"));
    }
    public UInventory getInventory() {
        if(gui == null) {
            gui = new UInventory(null, yml.getInt("size"), getTitle());
            final Inventory i = gui.getInventory();
            for(String s : yml.getConfigurationSection("gui").getKeys(false)) {
                i.setItem(yml.getInt("gui." + s + ".slot"), API.d(yml, "gui." + s));
            }
        }
        return gui;
    }
}
