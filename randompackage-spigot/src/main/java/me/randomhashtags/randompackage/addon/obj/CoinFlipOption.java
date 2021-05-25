package me.randomhashtags.randompackage.addon.obj;

import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public final class CoinFlipOption {
    public static HashMap<String, CoinFlipOption> PATHS;
    public final String path, chosen, selectionColor;
    public final int slot;
    private final ItemStack appear, selection;
    public CoinFlipOption(String path, int slot, String chosen, ItemStack appear, ItemStack selection, String selectionColor) {
        if(PATHS == null) {
            PATHS = new HashMap<>();
        }
        this.path = path;
        this.slot = slot;
        this.chosen = chosen;
        this.appear = appear;
        this.selection = selection;
        this.selectionColor = selectionColor;
        PATHS.put(path, this);
    }
    public ItemStack appear() {
        return appear.clone();
    }
    public ItemStack selection() {
        return selection.clone();
    }
}
