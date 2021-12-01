package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.JackpotData;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class JackpotDataObj implements JackpotData {
    private boolean notifications;
    private BigInteger totalTicketsBought, totalWins;
    private BigDecimal totalWonCash;

    public JackpotDataObj(boolean notifications, BigInteger totalTicketsBought, BigInteger totalWins, BigDecimal totalWonCash) {
        this.notifications = notifications;
        this.totalTicketsBought = totalTicketsBought;
        this.totalWins = totalWins;
        this.totalWonCash = totalWonCash;
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
    public BigInteger getTotalTicketsBought() {
        return totalTicketsBought;
    }

    @Override
    public void setTotalTicketsBought(BigInteger totalTicketsBought) {
        this.totalTicketsBought = totalTicketsBought;
    }

    @Override
    public BigInteger getTotalWins() {
        return totalWins;
    }

    @Override
    public void setTotalWins(BigInteger totalWins) {
        this.totalWins = totalWins;
    }

    @Override
    public BigDecimal getTotalWonCash() {
        return totalWonCash;
    }

    @Override
    public void setTotalWonCash(BigDecimal totalWonCash) {
        this.totalWonCash = totalWonCash;
    }
}
