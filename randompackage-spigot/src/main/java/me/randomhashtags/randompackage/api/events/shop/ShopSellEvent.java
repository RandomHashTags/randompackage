package me.randomhashtags.randompackage.api.events.shop;

import me.randomhashtags.randompackage.utils.classes.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ShopSellEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public final Player player;
    public final ShopItem shopitem;
    public final ItemStack item;
    public final int amount;
    public final double profit;
    public ShopSellEvent(Player player, ShopItem shopitem, ItemStack item, int amount, double profit) {
        this.player = player;
        this.shopitem = shopitem;
        this.item = item;
        this.amount = amount;
        this.profit = profit;
    }

    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
