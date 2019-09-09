package me.randomhashtags.randompackage.event;

import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class JackpotPurchaseTicketsEvent extends RPEventCancellable {
    public final BigDecimal amount, price;
    public JackpotPurchaseTicketsEvent(Player player, BigDecimal amount, BigDecimal price) {
        super(player);
        this.amount = amount;
        this.price = price;
    }
}
