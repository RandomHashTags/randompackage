package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.TreeMap;

public abstract class RarityGem extends Itemable {
    public static TreeMap<Integer, String> defaultColors;

    public abstract ItemStack getItem(int souls);
    public abstract List<EnchantRarity> getWorksFor();
    public abstract List<String> getSplitMsg();
    public abstract long getTimeBetweenSameKills();
    public abstract TreeMap<Integer, String> getColors();
    public abstract List<String> getToggleOnMsg();
    public abstract List<String> getToggleOffInteractMsg();
    public abstract List<String> getToggleOffDroppedMsg();
    public abstract List<String> getToggleOffMovedMsg();
    public abstract List<String> getToggleOffRanOutMsg();

    public String getColors(int soulsCollected) {
        final TreeMap<Integer, String> colors = getColors();
        if(soulsCollected < 100) return colors.get(0);
        int last = -1;
        for(int i = 100; i <= 1000000; i += 100) {
            if(soulsCollected >= i && soulsCollected < i + 100) {
                final String c = colors.get(i);
                final boolean d = c != null;
                if(d) last += 1;
                return d ? c : colors.get(last);
            }
        }
        return colors.get(-1);
    }

    public static String getColors(RarityGem gem, int soulsCollected) { return gem.getColors(soulsCollected); }
    public static RarityGem valueOf(ItemStack item) {
        if(raritygems != null && item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
            final List<String> l = item.getItemMeta().getLore();
            for(RarityGem g : raritygems.values())
                if(g.getItem().getItemMeta().getLore().equals(l))
                    return g;
        }
        return null;
    }
}
