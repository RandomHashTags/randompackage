package me.randomhashtags.randompackage.utils.classes;

import java.math.BigDecimal;

public class CoinFlipStats {
    public BigDecimal wins, losses, wonCash, lostCash, taxesPaid;
    public CoinFlipStats(BigDecimal wins, BigDecimal losses, BigDecimal wonCash, BigDecimal lostCash, BigDecimal taxesPaid) {
        this.wins = wins;
        this.losses = losses;
        this.wonCash = wonCash;
        this.lostCash = lostCash;
        this.taxesPaid = taxesPaid;
    }
}
