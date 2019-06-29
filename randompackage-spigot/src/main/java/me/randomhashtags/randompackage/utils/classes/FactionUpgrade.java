package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.utils.abstraction.AbstractFactionUpgrade;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class FactionUpgrade extends AbstractFactionUpgrade {
    public static TreeMap<String, FactionUpgrade> upgrades;

    private ItemStack item;

    public FactionUpgrade(File f) {
        if(upgrades == null) upgrades = new TreeMap<>();
        load(f);
        upgrades.put(getYamlName(), this);
    }

    public ItemStack getItem() {
        if(item == null) {
            item = api.d(yml, "item");
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
        return item.clone();
    }

    public void setPerks(String faction) {

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
        upgrades = null;
    }
}
