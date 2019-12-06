package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.InventoryPet;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class FileInventoryPet extends RPAddon implements InventoryPet {
    private ItemStack item, egg;
    private HashMap<Integer, Integer> cooldowns, requiredxp;
    public FileInventoryPet(File f) {
        load(f);
        if(isEnabled()) {
            register(Feature.INVENTORY_PET, this);
        }
    }
    public String getIdentifier() { return getYamlName(); }

    public boolean isEnabled() { return yml.getBoolean("settings.enabled"); }
    public int getMaxLevel() { return yml.getInt("settings.max level"); }
    public HashMap<Integer, Integer> getCooldowns() {
        if(cooldowns == null) {
            cooldowns = new HashMap<>();
            final ConfigurationSection c = yml.getConfigurationSection("settings.cooldown");
            if(c != null) {
                for(String s : c.getKeys(false)) {
                    final int cooldown = yml.getInt("settings.cooldown." + s)*1000;
                    if(s.equals("all")) {
                        cooldowns.put(-1, cooldown);
                    } else {
                        cooldowns.put(Integer.parseInt(s), cooldown);
                    }
                }
            }
        }
        return cooldowns;
    }
    public HashMap<Integer, Integer> getRequiredXp() {
        if(requiredxp == null) {
            requiredxp = new HashMap<>();
            final ConfigurationSection c = yml.getConfigurationSection("settings.exp to level");
            if(c != null) {
                for(String s : c.getKeys(false)) {
                    final int amount = yml.getInt("settings.exp to level." + s);
                    if(s.equals("all")) {
                        requiredxp.put(-1, amount);
                    } else {
                        requiredxp.put(Integer.parseInt(s), amount);
                    }
                }
            }
        }
        return requiredxp;
    }
    public String getOwner() {
        final String tex = yml.getString("item.texture");
        return tex != null ? tex : yml.getString("item.owner");
    }
    public ItemStack getItem() {
        if(item == null) {
            item = api.d(yml, "item");
            if(item != null) {
                final ItemMeta im = item.getItemMeta();
                item = getSkull(im.getDisplayName(), im.getLore(), LEGACY || THIRTEEN);
            }
        }
        return getClone(item);
    }

    public ItemStack getEgg() {
        if(egg == null) egg = api.d(yml, "egg");
        return getClone(egg);
    }
    public LinkedHashMap<InventoryPet, Integer> getEggRequiredPets() { return null; }
    public List<String> getAttributes() { return yml.getStringList("attributes"); }
}
