package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.addons.objects.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.math.BigDecimal;

public class ShopPrePurchaseEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public final Player player;
    public final ShopItem item;
    private int amount;
    private BigDecimal cost;
    public ShopPrePurchaseEvent(Player player, ShopItem item, int amount, BigDecimal cost) {
        this.player = player;
        this.item = item;
        this.amount = amount;
        this.cost = cost;
    }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
}
