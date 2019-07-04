package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public abstract class RandomizationScroll extends Itemable {
    public abstract List<EnchantRarity> getAppliesToRarities();
    public static RandomizationScroll valueOf(ItemStack is) {
        if(randomizationscrolls != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final ItemMeta m = is.getItemMeta();
            for(RandomizationScroll r : randomizationscrolls.values()) {
                if(r.getItem().getItemMeta().equals(m)) {
                    return r;
                }
            }
        }
        return null;
    }
}
