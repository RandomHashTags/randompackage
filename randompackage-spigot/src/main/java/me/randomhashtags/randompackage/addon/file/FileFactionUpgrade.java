package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.FactionUpgrade;
import me.randomhashtags.randompackage.addon.FactionUpgradeLevel;
import me.randomhashtags.randompackage.addon.FactionUpgradeType;
import me.randomhashtags.randompackage.addon.obj.FactionUpgradeLevelObj;
import me.randomhashtags.randompackage.enums.Feature;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FileFactionUpgrade extends RPAddon implements FactionUpgrade {
    private ItemStack item;
    private LinkedHashMap<Integer, FactionUpgradeLevel> levels;

    public FileFactionUpgrade(File f) {
        load(f);
        register(Feature.FACTION_UPGRADE, this);
    }
    public String getIdentifier() { return getYamlName(); }

    public ItemStack getItem() {
        if(item == null) {
            item = API.createItemStack(yml, "item");
            if(item != null) {
                final List<String> lore = item.getItemMeta().getLore(), format = getType().getFormat(), l = new ArrayList<>();
                final ItemMeta m = item.getItemMeta();
                for(String s : format) {
                    if(s.equals("{LORE}")) {
                        if(lore != null) l.addAll(lore);
                    } else {
                        l.add(s);
                    }
                }
                m.setLore(l);
                item.setItemMeta(m);
            }
        }
        return getClone(item);
    }
    public FactionUpgradeType getType() { return getFactionUpgradeType(yml.getString("settings.type")); }
    public int getSlot() { return yml.getInt("settings.slot"); }
    public boolean itemAmountEqualsTier() { return yml.getBoolean("settings.item amount=tier"); }

    public LinkedHashMap<Integer, FactionUpgradeLevel> getLevels() {
        if(levels == null) {
            levels = new LinkedHashMap<>();
            final ConfigurationSection c = yml.getConfigurationSection("levels");
            if(c != null) {
                levels.put(0, new FactionUpgradeLevelObj(0, -1, "", new ArrayList<>()));
                for(String s : c.getKeys(false)) {
                    final int i = Integer.parseInt(s);
                    final String p = "levels." + s + ".";
                    final FactionUpgradeLevelObj o = new FactionUpgradeLevelObj(i, yml.getDouble(p + "value"), yml.getString(p + "string"), yml.getStringList(p + "cost"));
                    levels.put(i, o);
                }
            }
        }
        return levels;
    }

    public List<String> getAttributes() { return yml.getStringList("attributes"); }
}
