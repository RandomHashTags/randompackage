package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class RarityFireball extends Itemable {
    public abstract ItemStack getRevealedItem(boolean usesChances);
    public abstract List<EnchantRarity> getExchangeableRarities();
    public abstract List<String> getReveals();

    public static RarityFireball valueOf(ItemStack is) {
        if(fireballs != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            for(RarityFireball f : fireballs.values())
                if(is.isSimilar(f.getItem()))
                    return f;
        }
        return null;
    }
    public static RarityFireball valueOf(List<EnchantRarity> exchangeablerarities) {
        if(fireballs != null) {
            for(RarityFireball f : fireballs.values()) {
                if(f.getExchangeableRarities().equals(exchangeablerarities)) {
                    return f;
                }
            }
        }
        return null;
    }
}
