package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.objects.KitItem;
import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public interface CustomKit extends Itemable {
    Kits getKitClass();
    int getSlot();
    int getMaxLevel();
    long getCooldown();
    List<KitItem> getItems();
    FallenHero getFallenHero();
    default String getFallenHeroName() {
        final FallenHero f = getFallenHero();
        return f != null ? f.getSpawnItem().getItemMeta().getDisplayName().replace("{NAME}", getItem().getItemMeta().getDisplayName()) : null;
    }
    default ItemStack getFallenHeroItem(CustomKit kit, boolean isSpawnItem) {
        final FallenHero f = kit.getFallenHero();
        final ItemStack is = f != null ? isSpawnItem ? f.getSpawnItem() : f.getGem() : null;
        if(is != null) {
            final String n = kit.getItem().getItemMeta().getDisplayName();
            final ItemMeta m = is.getItemMeta();
            m.setDisplayName(m.getDisplayName().replace("{NAME}", n));
            final List<String> l = new ArrayList<>();
            for(String s : m.getLore()) l.add(s.replace("{NAME}", n));
            m.setLore(l);
            is.setItemMeta(m);
        }
        return is;
    }
}
