package me.randomhashtags.randompackage.event;

import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public final class CoinFlipEndEvent extends AbstractEvent {
    public final OfflinePlayer winner, loser;
    public final BigDecimal wager, tax;
    public CoinFlipEndEvent(@NotNull OfflinePlayer winner, @NotNull OfflinePlayer loser, @NotNull BigDecimal wager, @NotNull BigDecimal tax) {
        this.winner = winner;
        this.loser = loser;
        this.wager = wager;
        this.tax = tax;
    }
}
