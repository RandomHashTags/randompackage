package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Identifyable;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class EnchantRarity extends Identifyable {
    public abstract String[] getRevealedEnchantRarities();
    public abstract List<String> getRevealedEnchantMsg();
    public abstract ItemStack getRevealItem();
    public abstract ItemStack getRevealedItem();
    public abstract String getNameColors();
    public abstract String getApplyColors();
    public abstract boolean percentsAddUpto100();
    public abstract String getSuccess();
    public abstract String getDestroy();
    public abstract List<String> getLoreFormat();
    public abstract int getSuccessSlot();
    public abstract int getDestroySlot();
    public abstract Firework getFirework();
    public abstract List<CustomEnchant> getEnchants();

    public static EnchantRarity valueOf(ItemStack is) {
        if(is != null && rarities != null) {
            for(EnchantRarity r : rarities.values()) {
                final ItemStack re = r.getRevealItem();
                if(re != null && re.isSimilar(is)) {
                    return r;
                }
            }
        }
        return null;
    }
    public static EnchantRarity valueOf(CustomEnchant enchant) {
        if(rarities != null) {
            for(EnchantRarity e : rarities.values()) {
                if(e.getEnchants().contains(enchant)) {
                    return e;
                }
            }
        }
        return null;
    }
}
