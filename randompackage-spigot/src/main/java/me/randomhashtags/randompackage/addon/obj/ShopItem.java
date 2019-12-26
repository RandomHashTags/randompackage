package me.randomhashtags.randompackage.addon.obj;

import me.randomhashtags.randompackage.universal.UMaterial;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.List;

public class ShopItem {
    public final String path, opensCategory;
    public final int slot;
    private final ItemStack display, purchasedItem;
    public final BigDecimal buyPrice, sellPrice;
    private List<String> executedCommands;
    public ShopItem(String path, int slot, String opensCategory, ItemStack display, ItemStack purchasedItem, BigDecimal buyPrice, BigDecimal sellPrice, List<String> executedCommands) {
        this.path = path;
        this.slot = slot;
        this.opensCategory = opensCategory;
        this.display = display;
        this.purchasedItem = purchasedItem;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.executedCommands = executedCommands;
    }

    public ItemStack getDisplay() { return display.clone(); }
    public ItemStack getPurchased() {
        final boolean d = purchasedItem != null;
        final ItemStack is = d ? purchasedItem.clone() : UMaterial.match(display).getItemStack();
        if(!d) is.setAmount(display.getAmount());
        return is;
    }
    public List<String> getExecutedCommands() { return executedCommands; }
}
