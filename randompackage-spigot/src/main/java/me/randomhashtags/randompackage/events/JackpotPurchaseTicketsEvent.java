package me.randomhashtags.randompackage.events;

import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class JackpotPurchaseTicketsEvent extends AbstractCancellable {
    public final Player player;
    public final BigDecimal amount, price;
    public JackpotPurchaseTicketsEvent(Player player, BigDecimal amount, BigDecimal price) {
        this.player = player;
        this.amount = amount;
        this.price = price;
    }
}
