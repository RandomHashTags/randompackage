package me.randomhashtags.randompackage.util.addon;

import me.randomhashtags.randompackage.addon.Trinket;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class FileTrinket extends RPAddon implements Trinket {
    private ItemStack item;
    private HashMap<String, String> settings;

    public FileTrinket(File f) {
        load(f);
        if(isEnabled()) addTrinket(this);
    }
    public String getIdentifier() { return getYamlName(); }

    public boolean isEnabled() { return Boolean.parseBoolean(getSetting("enabled", "false")); }
    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "item");
        return getClone(item);
    }
    public HashMap<String, String> getSettings() {
        if(settings == null) {
            settings = new HashMap<>();
            for(String s : yml.getConfigurationSection("settings").getKeys(false)) {
                settings.put(s, yml.getString("settings." + s));
            }
        }
        return settings;
    }
    public List<String> getAttributes() { return yml.getStringList("attributes"); }
}
