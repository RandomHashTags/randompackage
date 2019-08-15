package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import me.randomhashtags.randompackage.utils.addons.RPAddon;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class BlackScroll extends RPAddon implements Itemable {
    public abstract int getMinPercent();
    public abstract int getMaxPercent();
    public abstract List<EnchantRarity> getAppliesTo();

    public ItemStack getItem(int percent) {
        final ItemStack is = getItem();
        final ItemMeta m = is.getItemMeta();
        final List<String> l = new ArrayList<>();
        final String p = Integer.toString(percent > 100 ? 100 : percent);
        if(m.hasLore()) {
            for(String s : m.getLore()) {
                l.add(s.replace("{PERCENT}", p));
            }
            m.setLore(l);
        }
        is.setItemMeta(m);
        return is;
    }
    public ItemStack getItem(int min, int max) {
        final ItemStack is = getItem();
        final ItemMeta m = is.getItemMeta();
        final List<String> l = new ArrayList<>();
        final String mi = Integer.toString(min), ma = Integer.toString(max);
        if(m.hasLore()) {
            for(String s : m.getLore()) {
                l.add(s.replace("{MIN}", mi).replace("{MAX}", ma));
            }
            m.setLore(l);
        }
        is.setItemMeta(m);
        return is;
    }
    public ItemStack getRandomPercentItem() {
        return getItem(getRandomPercent());
    }
    public int getRandomPercent() {
        final int min = getMinPercent(), max = getMaxPercent();
        return min + random.nextInt(max-min+1);
    }

    public static BlackScroll valueOf(ItemStack is) {
        if(blackscrolls != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final Material m = is.getType();
            final String d = is.getItemMeta().getDisplayName();
            for(BlackScroll b : blackscrolls.values()) {
                final ItemStack i = b.getItem();
                if(m.equals(i.getType()) && is.getData().getData() == i.getData().getData() && d.equals(i.getItemMeta().getDisplayName())) {
                    return b;
                }
            }
        }
        return null;
    }
}
