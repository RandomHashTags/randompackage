package me.randomhashtags.randompackage.data;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public interface CoinFlipData {
    boolean receivesNotifications();
    void setReceivesNotifications(boolean notifications);
    BigDecimal getWins();
    void setWins(@NotNull BigDecimal wins);
    BigDecimal getLosses();
    void setLosses(@NotNull BigDecimal losses);
    BigDecimal getWonCash();
    void setWonCash(@NotNull BigDecimal wonCash);
    BigDecimal getLostCash();
    void setLostCash(@NotNull BigDecimal lostCash);
    BigDecimal getTaxesPaid();
    void setTaxesPaid(@NotNull BigDecimal taxesPaid);
}
