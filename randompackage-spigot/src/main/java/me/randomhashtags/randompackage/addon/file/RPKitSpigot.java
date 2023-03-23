package me.randomhashtags.randompackage.addon.file;

import me.randomhashtags.randompackage.addon.CustomKit;
import me.randomhashtags.randompackage.addon.FallenHero;
import me.randomhashtags.randompackage.addon.obj.KitItem;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class RPKitSpigot extends RPAddonSpigot implements CustomKit {
    private List<KitItem> items;

    public RPKitSpigot(@Nullable File file) {
        super(file);
    }

    public int getMaxLevel() {
        return yml.getInt("settings.max level");
    }
    public long getCooldown() {
        return yml.getLong("settings.cooldown");
    }
    public int getSlot() {
        return yml.getInt("gui settings.slot");
    }
    public FallenHero getFallenHero() {
        final String p = yml.getString("settings.fallen hero");
        return p != null ? getFallenHero(p) : null;
    }
    public List<KitItem> getItems() {
        if(items == null) {
            items = new ArrayList<>();
            for(String i : yml.getConfigurationSection("items").getKeys(false)) {
                final String t = yml.getString("items." + i + ".item");
                if(t != null) {
                    final int chance = yml.getInt("items." + i + ".chance", 100);
                    final KitItem k = new KitItem(this, i, yml.getString("items." + i + ".item"), yml.getString("items." + i + ".amount"), yml.getString("items." + i + ".name"), yml.getStringList("items." + i + ".lore"), chance, yml.getInt("items." + i + ".required level", 0));
                    items.add(k);
                }
            }
        }
        return items;
    }
    public void setItems(List<KitItem> items) {
        this.items = items;
    }
}
