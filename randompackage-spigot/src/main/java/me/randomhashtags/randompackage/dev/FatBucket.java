package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.util.RPItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface FatBucket extends Itemable, Attributable, RPItemStack {
    ItemStack getEmptyBucket();
    default ItemStack getItem(int usesLeft, int sourcesRequired) {
        final ItemStack is = getItem();
        setItem(is, usesLeft, sourcesRequired);
        return is;
    }
    default void setItem(ItemStack is, int usesLeft, int sourcesRequired) {
        setRPItemStackValues(is, new HashMap<String, String>() {{
            put("FatBucketInfo", usesLeft + ":" + sourcesRequired);
        }});
    }
}
