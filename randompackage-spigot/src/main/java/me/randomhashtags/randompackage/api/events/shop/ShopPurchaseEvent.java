package me.randomhashtags.randompackage.api.events.shop;

import me.randomhashtags.randompackage.utils.classes.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ShopPurchaseEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public final Player player;
    public final ShopItem shopitem;
    public final ItemStack item;
    public final int amount;
    public final double cost;
    public ShopPurchaseEvent(Player player, ShopItem shopitem, ItemStack item, int amount, double cost) {
        this.player = player;
        this.shopitem = shopitem;
        this.item = item;
        this.amount = amount;
        this.cost = cost;
    }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
