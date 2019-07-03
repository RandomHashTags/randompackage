package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class Mask extends Itemable {
    public abstract String getOwner();
    public abstract List<String> getAddedLore();

    public static Mask valueOf(ItemStack is) {
        if(masks != null && is != null && is.hasItemMeta()) {
            for(Mask m : masks.values()) {
                final ItemStack i = m.getItem();
                if(i.isSimilar(is))
                    return m;
            }
        }
        return null;
    }
    public static Mask getOnItem(ItemStack is) {
        if(masks != null) {
            final ItemMeta im = is != null ? is.getItemMeta() : null;
            if(im != null && im.hasLore()) {
                final List<String> l = im.getLore();
                for(Mask m : masks.values())
                    if(l.containsAll(m.getAddedLore()))
                        return m;
            }
        }
        return null;
    }
}
