package me.randomhashtags.randompackage.event;

import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;

public final class CoinFlipEndEvent extends AbstractEvent {
    public final OfflinePlayer winner, loser;
    public final BigDecimal wager, tax;
    public CoinFlipEndEvent(OfflinePlayer winner, OfflinePlayer loser, BigDecimal wager, BigDecimal tax) {
        this.winner = winner;
        this.loser = loser;
        this.wager = wager;
        this.tax = tax;
    }
}
