package me.randomhashtags.randompackage.api.events.shop;

import me.randomhashtags.randompackage.utils.classes.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ShopPrePurchaseEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public final Player player;
    public final ShopItem item;
    private int amount;
    private double cost;
    public ShopPrePurchaseEvent(Player player, ShopItem item, int amount, double cost) {
        this.player = player;
        this.item = item;
        this.amount = amount;
        this.cost = cost;
    }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
