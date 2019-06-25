package me.randomhashtags.randompackage.api.events.coinflip;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class CoinFlipEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    public final UUID winner, loser;
    public final long wager;
    public final double tax;
    public CoinFlipEndEvent(UUID winner, UUID loser, long wager, double tax) {
        this.winner = winner;
        this.loser = loser;
        this.wager = wager;
        this.tax = tax;
    }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
