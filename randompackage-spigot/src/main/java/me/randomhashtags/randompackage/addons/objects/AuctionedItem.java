package me.randomhashtags.randompackage.addons.objects;

import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AuctionedItem {
    public long auctionTime;
    public final UUID auctioner;
    private final ItemStack item;
    public final double price;
    public boolean claimable;
    public AuctionedItem(long auctionTime, UUID auctioner, ItemStack item, double price) {
        this.auctionTime = auctionTime;
        this.auctioner = auctioner;
        this.item = item;
        this.price = price;
    }
    public ItemStack item() { return item.clone(); }
}
