package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.utils.universal.UInventory;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.HashMap;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class FilterCategory {
    public static HashMap<String, FilterCategory> categories;

    private File f;
    private YamlConfiguration yml;
    private String ymlName, inventorytitle;
    private UInventory gui;
    public FilterCategory(File f) {
        if(categories == null) {
            categories = new HashMap<>();
        }
        this.f = f;
        ymlName = f.getName().split("\\.yml")[0];
        categories.put(ymlName, this);
    }

    public YamlConfiguration getYaml() {
        if(yml == null) yml = YamlConfiguration.loadConfiguration(f);
        return yml;
    }
    public String getYamlName() { return ymlName; }
    public String getInventoryTitle() {
        if(inventorytitle == null) inventorytitle = ChatColor.translateAlternateColorCodes('&', getYaml().getString("title"));
        return inventorytitle;
    }
    public UInventory getInventory() {
        if(gui == null) {
            getYaml();
            gui = new UInventory(null, yml.getInt("size"), getInventoryTitle());
            final Inventory i = gui.getInventory();
            for(String s : yml.getConfigurationSection("gui").getKeys(false)) {
                i.setItem(yml.getInt("gui." + s + ".slot"), api.d(yml, "gui." + s));
            }
        }
        return gui;
    }
    public static void deleteAll() {
        categories = null;
    }
}
