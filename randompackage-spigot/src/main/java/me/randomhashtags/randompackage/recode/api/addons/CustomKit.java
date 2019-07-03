package me.randomhashtags.randompackage.recode.api.addons;

import me.randomhashtags.randompackage.recode.RPAddon;
import me.randomhashtags.randompackage.recode.utils.KitItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class CustomKit extends RPAddon {
    public abstract ItemStack getItem();
    public abstract int getSlot();
    public abstract int getMaxLevel();
    public abstract long getCooldown();
    public abstract List<KitItem> getItems();
    public abstract FallenHero getFallenHero();
    public ItemStack getFallenHeroSpawnItem(CustomKit kit) {
        final FallenHero f = getFallenHero();
        return f != null ? get(kit, f.getSpawnItem()) : null;
    }
    public ItemStack getFallenHeroGemItem(CustomKit kit) {
        final FallenHero f = getFallenHero();
        return f != null ? get(kit, f.getGem()) : null;
    }
    private ItemStack get(CustomKit kit, ItemStack is) {
        final String n = kit.getItem().getItemMeta().getDisplayName();
        final ItemMeta m = is.getItemMeta();
        m.setDisplayName(m.getDisplayName().replace("{NAME}", n));
        final List<String> l = new ArrayList<>();
        for(String s : m.getLore()) l.add(s.replace("{NAME}", n));
        m.setLore(l);
        is.setItemMeta(m);
        return is;
    }

    public static CustomKit valueOf(int slot, Class type) {
        if(kits != null) {
            for(CustomKit k : kits.values()) {
                if(k.getClass().isInstance(type) && k.getSlot() == slot) {
                    return k;
                }
            }
        }
        return null;
    }
}
