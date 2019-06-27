package me.randomhashtags.randompackage.utils.abstraction;

import me.randomhashtags.randompackage.utils.classes.kits.FallenHero;
import me.randomhashtags.randompackage.utils.classes.kits.KitItem;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractCustomKit extends Saveable {
    private Set<KitItem> items;

    public int getSlot() { return yml.getInt("gui settings.slot"); }
    public int getMaxTier() { return yml.getInt("settings.max level"); }
    public long getCooldown() { return yml.getLong("settings.cooldown"); }
    public FallenHero getFallenHero() { return FallenHero.heroes.getOrDefault(yml.getString("settings.fallen hero"), null); }
    public Set<KitItem> getItems() {
        if(items == null) {
            items = new HashSet<>();
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
    public void setItems(Set<KitItem> items) { this.items = items; }
}
