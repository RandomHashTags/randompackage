package me.randomhashtags.randompackage.api.events.shop;

import me.randomhashtags.randompackage.utils.classes.shop.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ShopPreSellEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public final Player player;
    public final ShopItem item;
    private int amount;
    private double profit;
    public ShopPreSellEvent(Player player, ShopItem item, int amount, double profit) {
        this.player = player;
        this.item = item;
        this.amount = amount;
        this.profit = profit;
    }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public double getProfit() { return profit; }
    public void setProfit(double profit) { this.profit = profit; }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
