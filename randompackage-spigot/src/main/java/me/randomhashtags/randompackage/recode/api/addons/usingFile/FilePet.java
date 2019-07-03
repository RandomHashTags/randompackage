package me.randomhashtags.randompackage.recode.api.addons.usingFile;

import me.randomhashtags.randompackage.recode.api.addons.Pet;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.TreeMap;

public class FilePet extends Pet {
    private ItemStack item;
    public FilePet(File f) {
        load(f);
        initilize();
    }
    public void initilize() { addPet(getYamlName(), this); }

    public TreeMap<Integer, Long> getCooldownForLevel() {
        final TreeMap<Integer, Long> a = new TreeMap<>();
        final ConfigurationSection c = yml.getConfigurationSection("settings.cooldowns");
        if(c != null) {
            for(String s : c.getKeys(false)) {
                a.put(Integer.parseInt(s), yml.getLong("settings.cooldowns." + s));
            }
        }
        return a;
    }
    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "item");
        return item.clone();
    }
    public TreeMap<Integer, Long> getRequiredXpForLevel() {
        final TreeMap<Integer, Long> a = new TreeMap<>();
        final ConfigurationSection c = yml.getConfigurationSection("settings.exp to level");
        if(c != null) {
            for(String s : c.getKeys(false)) {
                a.put(Integer.parseInt(s), yml.getLong("settings.exp to level." + s));
            }
        }
        return a;
    }
}
