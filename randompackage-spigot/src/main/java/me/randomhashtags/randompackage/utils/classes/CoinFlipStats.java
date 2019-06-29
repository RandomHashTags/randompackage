package me.randomhashtags.randompackage.utils.classes;

public class CoinFlipStats {
    public long wins, losses, wonCash, lostCash, taxesPaid;
    public CoinFlipStats(long wins, long losses, long wonCash, long lostCash, long taxesPaid) {
        this.wins = wins;
        this.losses = losses;
        this.wonCash = wonCash;
        this.lostCash = lostCash;
        this.taxesPaid = taxesPaid;
    }
}
