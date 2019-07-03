package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.recode.api.events.AbstractEvent;
import org.bukkit.entity.Player;

public class JackpotPurchaseTicketsEvent extends AbstractEvent {
    public final Player player;
    public final int amount;
    public final double price;
    public JackpotPurchaseTicketsEvent(Player player, int amount, double price) {
        this.player = player;
        this.amount = amount;
        this.price = price;
    }
}
