package me.randomhashtags.randompackage.utils.classes.shop;

import org.spongepowered.api.item.inventory.ItemStack;

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

    public ItemStack getDisplay() { return display.copy(); }
    public ItemStack getPurchased() {
        final boolean d = purchasedItem != null;
        final ItemStack is = d ? purchasedItem.copy() : UMaterial.match(display).getItemStack();
        if(!d) is.setQuantity(display.getQuantity());
        return is;
    }
}
