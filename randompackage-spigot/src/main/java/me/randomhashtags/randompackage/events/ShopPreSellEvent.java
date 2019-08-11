package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.objects.ShopItem;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.math.BigDecimal;

public class ShopPreSellEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public final Player player;
    public final ShopItem item;
    private int amount;
    private BigDecimal profit;
    public ShopPreSellEvent(Player player, ShopItem item, int amount, BigDecimal profit) {
        this.player = player;
        this.item = item;
        this.amount = amount;
        this.profit = profit;
    }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public BigDecimal getProfit() { return profit; }
    public void setProfit(BigDecimal profit) { this.profit = profit; }

    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
}
