package me.randomhashtags.randompackage.event;

import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.math.BigInteger;

public class JackpotPurchaseTicketsEvent extends RPEventCancellable {
    public final BigInteger amount;
    public final BigDecimal price;
    public JackpotPurchaseTicketsEvent(Player player, BigInteger amount, BigDecimal price) {
        super(player);
        this.amount = amount;
        this.price = price;
    }
}
