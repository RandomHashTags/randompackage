package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.JackpotData;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class JackpotDataObj implements JackpotData {
    private boolean notifications;
    private BigInteger totalTicketsBought, totalWins;
    private BigDecimal totalWonCash;

    public JackpotDataObj(boolean notifications, @NotNull BigInteger totalTicketsBought, @NotNull BigInteger totalWins, @NotNull BigDecimal totalWonCash) {
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
    public @NotNull BigInteger getTotalTicketsBought() {
        return totalTicketsBought;
    }

    @Override
    public void setTotalTicketsBought(@NotNull BigInteger totalTicketsBought) {
        this.totalTicketsBought = totalTicketsBought;
    }

    @Override
    public @NotNull BigInteger getTotalWins() {
        return totalWins;
    }

    @Override
    public void setTotalWins(@NotNull BigInteger totalWins) {
        this.totalWins = totalWins;
    }

    @Override
    public @NotNull BigDecimal getTotalWonCash() {
        return totalWonCash;
    }

    @Override
    public void setTotalWonCash(@NotNull BigDecimal totalWonCash) {
        this.totalWonCash = totalWonCash;
    }
}
