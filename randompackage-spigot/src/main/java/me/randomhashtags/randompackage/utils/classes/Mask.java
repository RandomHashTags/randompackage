package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.utils.abstraction.AbstractMask;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.HashMap;
import java.util.List;


public class Mask extends AbstractMask {
    public static HashMap<String, Mask> masks;

    public Mask(File f) {
        if(masks == null) masks = new HashMap<>();
        load(f);
        masks.put(getYamlName(), this);
    }

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

    public static void deleteAll() {
        masks = null;
    }
}
