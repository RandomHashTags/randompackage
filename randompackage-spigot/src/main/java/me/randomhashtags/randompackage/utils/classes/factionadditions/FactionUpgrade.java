package me.randomhashtags.randompackage.utils.classes.factionadditions;

import me.randomhashtags.randompackage.RandomPackageAPI;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackageAPI.rpd;

public class FactionUpgrade {
    public static YamlConfiguration yml;
    private static RandomPackageAPI api;
    public static HashMap<String, FactionUpgrade> upgrades;

    private String path;
    private FactionUpgradeType type;
    private int slot, maxtier;
    private ItemStack item;
    private List<String> description, perks, requirements;
    public FactionUpgrade(String path, FactionUpgradeType type) {
        if(upgrades == null) {
            upgrades = new HashMap<>();
            api = RandomPackageAPI.getAPI();
            yml = YamlConfiguration.loadConfiguration(new File(rpd, "faction additions.yml"));
        }
        this.path = path;
        this.type = type;
        slot = yml.getInt("upgrades." + path + ".slot");
        maxtier = yml.getInt("upgrades." + path + ".max tier");
        upgrades.put(path, this);
    }

    public String getPath() { return path; }
    public FactionUpgradeType getUpgradeType() { return type; }
    public int getSlot() { return slot; }
    public int getMaxTier() { return maxtier; }
    public ItemStack getItem() {
        if(item == null) {
            final ItemStack is = api.d(yml, "upgrades." + path);
            final ItemMeta m = is.getItemMeta();
            final List<String> L = new ArrayList<>();
            for(String s : type.getFormat()) {
                if(s.equals("{DESC}")) L.addAll(getDescription());
                else L.add(s);
            }
            m.setLore(L);
            is.setItemMeta(m);
            item = is;
        }
        return item.clone();
    }
    public List<String> getDescription() {
        if(description == null) description = api.colorizeListString(yml.getStringList("upgrades." + path + ".desc"));
        return description;
    }
    public List<String> getPerks() {
        if(perks == null) perks = yml.getStringList("upgrades." + path + ".perks");
        return perks;
    }
    public List<String> getRequirements() {
        if(requirements == null) requirements = yml.getStringList("upgrades." + path + ".requirements");
        return requirements;
    }

    public static FactionUpgrade valueOf(int slot) {
        if(upgrades != null) {
            for(FactionUpgrade f : upgrades.values()) {
                if(f.getSlot() == slot) {
                    return f;
                }
            }
        }
        return null;
    }

    public static void deleteAll() {
        yml = null;
        api = null;
        upgrades = null;
    }
}
