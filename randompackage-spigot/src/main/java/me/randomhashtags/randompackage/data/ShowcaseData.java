package me.randomhashtags.randompackage.data;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface ShowcaseData {
    HashMap<Integer, Integer> getShowcaseSizes();
    default int getShowcaseSize(int page) {
        final HashMap<Integer, Integer> sizes = getShowcaseSizes();
        return sizes != null ? sizes.getOrDefault(page, -1) : -1;
    }
    HashMap<Integer, ItemStack[]> getShowcases();
    default ItemStack[] getShowcase(int page) {
        final HashMap<Integer, ItemStack[]> showcases = getShowcases();
        return showcases != null ? showcases.getOrDefault(page, null) : null;
    }
}
