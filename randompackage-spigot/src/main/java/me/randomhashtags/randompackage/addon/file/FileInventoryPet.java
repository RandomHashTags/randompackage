package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.InventoryPet;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public final class FileInventoryPet extends RPAddonSpigot implements InventoryPet {
    private ItemStack item, egg;
    private HashMap<Integer, String> values;
    private HashMap<Integer, Integer> cooldowns, requiredxp;
    public FileInventoryPet(File f) {
        super(f);
        register(Feature.INVENTORY_PET, this);
    }

    public boolean isEnabled() {
        return yml.getBoolean("settings.enabled");
    }
    public int getMaxLevel() {
        return yml.getInt("settings.max level");
    }
    public HashMap<Integer, String> getValues() {
        if(values == null) {
            values = new HashMap<>();
            for(String level : getConfigurationSectionKeys(yml, "settings.values", false)) {
                values.put(Integer.parseInt(level), yml.getString("settings.values." + level));
            }
        }
        return values;
    }
    public HashMap<Integer, Integer> getCooldowns() {
        if(cooldowns == null) {
            cooldowns = new HashMap<>();
            for(String s : getConfigurationSectionKeys(yml, "settings.cooldown", false)) {
                final int cooldown = yml.getInt("settings.cooldown." + s)*1000;
                if(s.equals("all")) {
                    cooldowns.put(-1, cooldown);
                } else {
                    cooldowns.put(Integer.parseInt(s), cooldown);
                }
            }
        }
        return cooldowns;
    }
    public HashMap<Integer, Integer> getRequiredXp() {
        if(requiredxp == null) {
            requiredxp = new HashMap<>();
            for(String s : getConfigurationSectionKeys(yml, "settings.exp to level", false)) {
                final int amount = yml.getInt("settings.exp to level." + s);
                if(s.equals("all")) {
                    requiredxp.put(-1, amount);
                } else {
                    requiredxp.put(Integer.parseInt(s), amount);
                }
            }
        }
        return requiredxp;
    }
    public String getOwner() {
        final String tex = yml.getString("item.texture");
        return tex != null ? tex : yml.getString("item.owner");
    }
    @NotNull
    @Override
    public ItemStack getItem() {
        if(item == null) {
            item = createItemStack(yml, "item");
            if(item != null) {
                final ItemMeta im = item.getItemMeta();
                item = getSkull(im.getDisplayName(), im.getLore(), LEGACY || THIRTEEN);
                set_inventory_pet_values(item, identifier, 1, 0, 0);
            }
        }
        return getClone(item);
    }

    public ItemStack getEgg() {
        if(egg == null) egg = createItemStack(yml, "egg");
        return getClone(egg);
    }
    public LinkedHashMap<InventoryPet, Integer> getEggRequiredPets() {
        return null;
    }
    public List<String> getAttributes() {
        return getStringList(yml, "attributes");
    }
}
