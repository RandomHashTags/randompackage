package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.FilterCategory;
import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

import java.io.File;

public class FileFilterCategory extends FilterCategory {
    private UInventory gui;
    public FileFilterCategory(File f) {
        load(f);
        addFilterCategory(getIdentifier(), this);
    }
    public String getIdentifier() { return getYamlName(); }

    public String getTitle() {
        return ChatColor.translateAlternateColorCodes('&', yml.getString("title"));
    }
    public UInventory getInventory() {
        if(gui == null) {
            gui = new UInventory(null, yml.getInt("size"), getTitle());
            final Inventory i = gui.getInventory();
            for(String s : yml.getConfigurationSection("gui").getKeys(false)) {
                i.setItem(yml.getInt("gui." + s + ".slot"), api.d(yml, "gui." + s));
            }
        }
        return gui;
    }
}
