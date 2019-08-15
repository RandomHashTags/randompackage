package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import me.randomhashtags.randompackage.utils.addons.RPAddon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class MagicDust extends RPAddon implements Itemable {
    public abstract int getChance();
    public abstract int getMinPercent();
    public abstract int getMaxPercent();
    public abstract List<EnchantRarity> getAppliesTo();
    public abstract MagicDust getUpgradesTo();
    public abstract double getUpgradeCost();

    public ItemStack getItem(int percent) {
        final String p = Integer.toString(percent);
        final ItemStack i = getItem();
        final ItemMeta m = i.getItemMeta();
        final List<String> l = new ArrayList<>();
        for(String s : m.getLore()) {
            l.add(s.replace("{PERCENT}", p));
        }
        m.setLore(l);
        i.setItemMeta(m);
        return i;
    }
    public ItemStack getRandomPercentItem() {
        final int min = getMinPercent(), max = getMaxPercent();
        return getItem(min+(random.nextInt(max-min+1)));
    }

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
