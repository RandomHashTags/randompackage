package me.randomhashtags.randompackage.data;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public interface CoinFlipData {
    boolean receivesNotifications();
    void setReceivesNotifications(boolean notifications);
    @NotNull BigDecimal getWins();
    void setWins(@NotNull BigDecimal wins);
    @NotNull BigDecimal getLosses();
    void setLosses(@NotNull BigDecimal losses);
    @NotNull BigDecimal getWonCash();
    void setWonCash(@NotNull BigDecimal wonCash);
    @NotNull BigDecimal getLostCash();
    void setLostCash(@NotNull BigDecimal lostCash);
    @NotNull BigDecimal getTaxesPaid();
    void setTaxesPaid(@NotNull BigDecimal taxesPaid);
}
