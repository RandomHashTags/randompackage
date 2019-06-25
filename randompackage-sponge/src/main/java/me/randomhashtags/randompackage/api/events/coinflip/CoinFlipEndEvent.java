package me.randomhashtags.randompackage.api.events.coinflip;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;

import java.util.UUID;

public class CoinFlipEndEvent extends RandomPackageEvent {
    public final UUID winner, loser;
    public final long wager;
    public final double tax;
    public CoinFlipEndEvent(UUID winner, UUID loser, long wager, double tax) {
        this.winner = winner;
        this.loser = loser;
        this.wager = wager;
        this.tax = tax;
    }
}
