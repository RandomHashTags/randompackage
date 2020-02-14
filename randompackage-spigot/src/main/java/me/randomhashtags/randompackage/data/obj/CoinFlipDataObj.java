package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.CoinFlipData;

import java.math.BigDecimal;

public class CoinFlipDataObj implements CoinFlipData {
    private boolean notifications;
    private BigDecimal wins, losses, wonCash, lostCash, taxesPaid;
    public CoinFlipDataObj(boolean notifications, BigDecimal wins, BigDecimal losses, BigDecimal wonCash, BigDecimal lostCash, BigDecimal taxesPaid) {
        this.notifications = notifications;
        this.wins = wins;
        this.losses = losses;
        this.wonCash = wonCash;
        this.lostCash = lostCash;
        this.taxesPaid = taxesPaid;
    }

    public boolean receivesNotifications() {
        return notifications;
    }
    public void setReceivesNotifications(boolean notifications) {
        this.notifications = notifications;
    }
    public BigDecimal getWins() {
        return wins;
    }
    public void setWins(BigDecimal wins) {
        this.wins = wins;
    }
    public BigDecimal getLosses() {
        return losses;
    }
    public void setLosses(BigDecimal losses) {
        this.losses = losses;
    }
    public BigDecimal getWonCash() {
        return wonCash;
    }
    public void setWonCash(BigDecimal wonCash) {
        this.wonCash = wonCash;
    }
    public BigDecimal getLostCash() {
        return lostCash;
    }
    public void setLostCash(BigDecimal lostCash) {
        this.lostCash = lostCash;
    }
    public BigDecimal getTaxesPaid() {
        return taxesPaid;
    }
    public void setTaxesPaid(BigDecimal taxesPaid) {
        this.taxesPaid = taxesPaid;
    }
}
