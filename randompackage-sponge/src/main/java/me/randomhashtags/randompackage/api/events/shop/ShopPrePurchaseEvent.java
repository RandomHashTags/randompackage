package me.randomhashtags.randompackage.api.events.shop;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.shop.ShopItem;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;

public class ShopPrePurchaseEvent extends RandomPackageEvent implements Cancellable {
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
}
