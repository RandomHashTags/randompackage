package me.randomhashtags.randompackage.recode.api.addons;

import me.randomhashtags.randompackage.recode.utils.KitItem;

import java.util.ArrayList;
import java.util.List;

public abstract class RPKit extends CustomKit {
    private List<KitItem> items;
    public int getMaxLevel() { return yml.getInt("settings.max level"); }
    public long getCooldown() { return yml.getLong("settings.cooldown"); }
    public int getSlot() { return yml.getInt("gui settings.slot"); }
    public FallenHero getFallenHero() { return null; }
    public List<KitItem> getItems() {
        if(items == null) {
            items = new ArrayList<>();
            for(String i : yml.getConfigurationSection("items").getKeys(false)) {
                final String t = yml.getString("items." + i + ".item");
                if(t != null) {
                    final int chance = yml.get("items." + i + ".chance") != null ? yml.getInt("items." + i + ".chance") : 100;
                    items.add(new KitItem(this, i, yml.getString("items." + i + ".item"), yml.getString("items." + i + ".name"), yml.getStringList("items." + i + ".lore"), chance, "1", false, yml.getInt("items." + i + ".reqlevel")));
                }
            }
        }
        return items;
    }

}
