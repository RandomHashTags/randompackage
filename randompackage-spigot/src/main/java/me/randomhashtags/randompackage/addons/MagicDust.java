package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class MagicDust extends Itemable {
    public abstract int getChance();
    public abstract int getMinPercent();
    public abstract int getMaxPercent();
    public abstract ItemStack getItem(int percent);
    public abstract ItemStack getRandomPercentItem();
    public abstract List<EnchantRarity> getAppliesTo();
    public abstract MagicDust getUpgradesTo();
    public abstract double getUpgradeCost();

    public static MagicDust valueOf(ItemStack is) {
        if(dusts != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final Material m = is.getType();
            final String d = is.getItemMeta().getDisplayName();
            for(MagicDust dust : dusts.values()) {
                final ItemStack i = dust.getItem();
                if(i.getType().equals(m) && i.getItemMeta().getDisplayName().equals(d)) return dust;
            }
        }
        return null;
    }
}
