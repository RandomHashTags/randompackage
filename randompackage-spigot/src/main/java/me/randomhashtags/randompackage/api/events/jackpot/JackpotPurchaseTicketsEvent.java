package me.randomhashtags.randompackage.api.events.jackpot;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class JackpotPurchaseTicketsEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public final Player player;
    public final int amount;
    public final double price;
    public JackpotPurchaseTicketsEvent(Player player, int amount, double price) {
        this.player = player;
        this.amount = amount;
        this.price = price;
    }

    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
