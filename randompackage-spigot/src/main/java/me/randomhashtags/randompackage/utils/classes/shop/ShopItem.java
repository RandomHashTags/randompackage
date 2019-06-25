package me.randomhashtags.randompackage.utils.classes.shop;

import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.inventory.ItemStack;

public class ShopItem {
    public final String path, opensCategory;
    public final int slot;
    private final ItemStack display, purchasedItem;
    public final double buyPrice, sellPrice;
    public ShopItem(String path, int slot, String opensCategory, ItemStack display, ItemStack purchasedItem, double buyPrice, double sellPrice) {
        this.path = path;
        this.slot = slot;
        this.opensCategory = opensCategory;
        this.display = display;
        this.purchasedItem = purchasedItem;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
    }

    public ItemStack getDisplay() { return display.clone(); }
    public ItemStack getPurchased() {
        final boolean d = purchasedItem != null;
        final ItemStack is = d ? purchasedItem.clone() : UMaterial.match(display).getItemStack();
        if(!d) is.setAmount(display.getAmount());
        return is;
    }
}
