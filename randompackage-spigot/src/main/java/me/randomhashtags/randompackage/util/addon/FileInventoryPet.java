package me.randomhashtags.randompackage.util.addon;

import me.randomhashtags.randompackage.dev.InventoryPet;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

public class FileInventoryPet extends RPAddon implements InventoryPet {
    private ItemStack item, egg;
    public FileInventoryPet(File f) {
        load(f);
        addPet(this);
    }
    public String getIdentifier() { return getYamlName(); }

    public int getCooldownSlot() { return get("{COOLDOWN}"); }
    public int getExpSlot() { return get("{EXP}"); }
    private int get(String input) {
        final List<String> l = getItem().getItemMeta().getLore();
        for(int i = 0; i < l.size(); i++) {
            if(l.get(i).contains(input)) {
                return i;
            }
        }
        return -1;
    }
    public TreeMap<Integer, Long> getCooldowns() {
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
    public TreeMap<Integer, Long> getRequiredXp() {
        final TreeMap<Integer, Long> a = new TreeMap<>();
        final ConfigurationSection c = yml.getConfigurationSection("settings.exp to level");
        if(c != null) {
            for(String s : c.getKeys(false)) {
                a.put(Integer.parseInt(s), yml.getLong("settings.exp to level." + s));
            }
        }
        return a;
    }
    public ItemStack getEgg() {
        if(egg == null) egg = api.d(yml, "egg");
        return egg != null ? egg.clone() : null;
    }
    public LinkedHashMap<InventoryPet, Integer> getEggRequiredPets() { return null; }

    public List<String> getAttributes() { return yml.getStringList("attributes"); }
}
