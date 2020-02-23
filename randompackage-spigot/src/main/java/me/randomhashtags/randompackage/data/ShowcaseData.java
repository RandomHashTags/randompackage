package me.randomhashtags.randompackage.data;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface ShowcaseData {
    HashMap<Integer, Integer> getSizes();
    void setSizes(HashMap<Integer, Integer> showcaseSizes);
    default int getSize(int page) {
        final HashMap<Integer, Integer> sizes = getSizes();
        return sizes != null ? sizes.getOrDefault(page, -1) : -1;
    }

    HashMap<Integer, ItemStack[]> getShowcases();
    void setShowcases(HashMap<Integer, ItemStack[]> showcases);
    default ItemStack[] getShowcaseItems(int page) {
        final HashMap<Integer, ItemStack[]> showcases = getShowcases();
        return showcases != null ? showcases.getOrDefault(page, null) : null;
    }

    default void reset() {
        setShowcases(new HashMap<>());
        setSizes(new HashMap<>());
    }
    default void reset(int page) {
        final HashMap<Integer, ItemStack[]> showcases = getShowcases();
        final HashMap<Integer, Integer> sizes = getSizes();
        showcases.put(page, new ItemStack[54]);
        sizes.put(page, 9);
    }
}
