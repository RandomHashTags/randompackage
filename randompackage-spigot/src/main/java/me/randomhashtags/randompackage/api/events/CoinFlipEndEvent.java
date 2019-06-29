package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.utils.abstraction.AbstractEvent;

import java.math.BigDecimal;
import java.util.UUID;

public class CoinFlipEndEvent extends AbstractEvent {
    public final UUID winner, loser;
    public final BigDecimal wager, tax;
    public CoinFlipEndEvent(UUID winner, UUID loser, BigDecimal wager, BigDecimal tax) {
        this.winner = winner;
        this.loser = loser;
        this.wager = wager;
        this.tax = tax;
    }
}
