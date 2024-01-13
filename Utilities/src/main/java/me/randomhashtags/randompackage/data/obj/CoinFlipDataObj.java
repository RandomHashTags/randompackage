package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.CoinFlipData;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public final class CoinFlipDataObj implements CoinFlipData {
    private boolean notifications;
    private BigDecimal wins, losses, wonCash, lostCash, taxesPaid;

    public CoinFlipDataObj(boolean notifications, @NotNull BigDecimal wins, @NotNull BigDecimal losses, @NotNull BigDecimal wonCash, @NotNull BigDecimal lostCash, @NotNull BigDecimal taxesPaid) {
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
    public @NotNull BigDecimal getWins() {
        return wins;
    }
    @Override
    public void setWins(@NotNull BigDecimal wins) {
        this.wins = wins;
    }

    @Override
    public @NotNull BigDecimal getLosses() {
        return losses;
    }
    @Override
    public void setLosses(@NotNull BigDecimal losses) {
        this.losses = losses;
    }

    @Override
    public @NotNull BigDecimal getWonCash() {
        return wonCash;
    }
    @Override
    public void setWonCash(@NotNull BigDecimal wonCash) {
        this.wonCash = wonCash;
    }

    @Override
    public @NotNull BigDecimal getLostCash() {
        return lostCash;
    }
    @Override
    public void setLostCash(@NotNull BigDecimal lostCash) {
        this.lostCash = lostCash;
    }

    @Override
    public @NotNull BigDecimal getTaxesPaid() {
        return taxesPaid;
    }
    @Override
    public void setTaxesPaid(@NotNull BigDecimal taxesPaid) {
        this.taxesPaid = taxesPaid;
    }
}
