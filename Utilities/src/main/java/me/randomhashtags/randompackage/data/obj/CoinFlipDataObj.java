package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.CoinFlipData;

import java.math.BigDecimal;

public final class CoinFlipDataObj implements CoinFlipData {
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

    @Override
    public boolean receivesNotifications() {
        return notifications;
    }
    @Override
    public void setReceivesNotifications(boolean notifications) {
        this.notifications = notifications;
    }

    @Override
    public BigDecimal getWins() {
        return wins;
    }
    @Override
    public void setWins(BigDecimal wins) {
        this.wins = wins;
    }

    @Override
    public BigDecimal getLosses() {
        return losses;
    }
    @Override
    public void setLosses(BigDecimal losses) {
        this.losses = losses;
    }

    @Override
    public BigDecimal getWonCash() {
        return wonCash;
    }
    @Override
    public void setWonCash(BigDecimal wonCash) {
        this.wonCash = wonCash;
    }

    @Override
    public BigDecimal getLostCash() {
        return lostCash;
    }
    @Override
    public void setLostCash(BigDecimal lostCash) {
        this.lostCash = lostCash;
    }

    @Override
    public BigDecimal getTaxesPaid() {
        return taxesPaid;
    }
    @Override
    public void setTaxesPaid(BigDecimal taxesPaid) {
        this.taxesPaid = taxesPaid;
    }
}
