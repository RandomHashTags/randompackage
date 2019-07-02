package me.randomhashtags.randompackage.utils.classes.kits;

import me.randomhashtags.randompackage.utils.abstraction.AbstractCustomKit;
import me.randomhashtags.randompackage.utils.abstraction.AbstractFallenHero;
import me.randomhashtags.randompackage.utils.universal.UVersion;

import java.util.ArrayList;
import java.util.List;

public class CustomKit extends AbstractCustomKit {
    private List<KitItem> items;

    public int getSlot() { return yml.getInt("gui settings.slot"); }
    public int getMaxLevel() { return yml.getInt("settings.max level"); }
    public long getCooldown() { return yml.getLong("settings.cooldown"); }
    public AbstractFallenHero getFallenHero() {
        final AbstractFallenHero f = UVersion.getUVersion().getFallenHero(null, yml.getString("settings.fallen hero"));
        return f;
    }
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
    public void setItems(List<KitItem> items) { this.items = items; }
}
