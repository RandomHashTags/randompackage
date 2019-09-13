package me.randomhashtags.randompackage.util.addon;

import me.randomhashtags.randompackage.addon.util.Skullable;
import me.randomhashtags.randompackage.addon.InventoryPet;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class FileInventoryPet extends RPAddon implements InventoryPet, Skullable {
    private ItemStack item, egg;
    public FileInventoryPet(File f) {
        load(f);
        if(isEnabled()) addPet(this);
    }
    public String getIdentifier() { return getYamlName(); }

    public boolean isEnabled() { return yml.getBoolean("settings.enabled"); }
    public int getMaxLevel() { return yml.getInt("settings.max level"); }
    public HashMap<Integer, Long> getCooldowns() {
        final HashMap<Integer, Long> a = new HashMap<>();
        final ConfigurationSection c = yml.getConfigurationSection("settings.cooldown");
        if(c != null) {
            for(String s : c.getKeys(false)) {
                a.put(Integer.parseInt(s), yml.getLong("settings.cooldown." + s)*1000);
            }
        }
        return a;
    }
    public HashMap<Integer, Long> getRequiredXp() {
        final HashMap<Integer, Long> a = new HashMap<>();
        final ConfigurationSection c = yml.getConfigurationSection("settings.exp to level");
        if(c != null) {
            for(String s : c.getKeys(false)) {
                a.put(Integer.parseInt(s), yml.getLong("settings.exp to level." + s));
            }
        }
        return a;
    }
    public String getOwner() {
        final String tex = yml.getString("item.texture");
        return tex != null ? tex : yml.getString("item.owner");
    }
    public ItemStack getItem() {
        if(item == null) {
            item = api.d(yml, "item");
        }
        return item.clone();
    }

    public ItemStack getEgg() {
        if(egg == null) egg = api.d(yml, "egg");
        return egg != null ? egg.clone() : null;
    }
    public LinkedHashMap<InventoryPet, Integer> getEggRequiredPets() { return null; }

    public List<String> getAttributes() { return yml.getStringList("attributes"); }
}
