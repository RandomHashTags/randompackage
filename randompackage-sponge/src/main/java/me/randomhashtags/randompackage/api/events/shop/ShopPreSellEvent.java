package me.randomhashtags.randompackage.api.events.shop;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.shop.ShopItem;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;

public class ShopPreSellEvent extends RandomPackageEvent implements Cancellable {
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
}
