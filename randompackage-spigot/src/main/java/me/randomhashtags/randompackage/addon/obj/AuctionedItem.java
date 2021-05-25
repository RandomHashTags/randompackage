package me.randomhashtags.randompackage.addon.obj;

import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;
import java.util.UUID;

public final class AuctionedItem {
    public long auctionTime;
    public final UUID auctioner;
    private final ItemStack item;
    public final BigDecimal price;
    public boolean claimable;
    public AuctionedItem(long auctionTime, UUID auctioner, ItemStack item, BigDecimal price) {
        this.auctionTime = auctionTime;
        this.auctioner = auctioner;
        this.item = item;
        this.price = price;
    }
    public ItemStack item() {
        return item.clone();
    }
}
