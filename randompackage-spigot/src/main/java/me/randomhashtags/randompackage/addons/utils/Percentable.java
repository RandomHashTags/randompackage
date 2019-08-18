package me.randomhashtags.randompackage.addons.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public interface Percentable extends Itemable {
    int getMinPercent();
    int getMaxPercent();
    default ItemStack getItem(final int percent) {
        final String p = Integer.toString(percent);
        final ItemStack is = getItem();
        final ItemMeta m = is.getItemMeta();
        final List<String> l = new ArrayList<>();
        for(String s : m.getLore()) {
            l.add(s.replace("{PERCENT}", p));
        }
        m.setLore(l);
        is.setItemMeta(m);
        return is;
    }
    default ItemStack getItem(final int min, final int max) {
        final String mi = Integer.toString(min), ma = Integer.toString(max);
        final ItemStack is = getItem();
        final ItemMeta m = is.getItemMeta();
        final List<String> l = new ArrayList<>();
        for(String s : m.getLore()) {
            l.add(s.replace("{MIN_PERCENT}", mi).replace("{MAX_PERCENT}", ma));
        }
        m.setLore(l);
        is.setItemMeta(m);
        return is;
    }
    default ItemStack getRandomPercentItem(final Random random) { return getRandomPercentItem(random, true); }
    default ItemStack getRandomPercentItem(final Random random, final boolean isExact) {
        final int min = getRandomPercent(random);
        return isExact ? getItem(min) : getItem(min, getRandomPercent(random));
    }
    default int getRandomPercent(final Random random) {
        final int min = getMinPercent();
        return min+random.nextInt(getMaxPercent()-min+1);
    }
}
