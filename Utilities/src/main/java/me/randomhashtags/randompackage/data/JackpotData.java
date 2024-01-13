package me.randomhashtags.randompackage.data;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface JackpotData {
    boolean receivesNotifications();
    void setReceivesNotifications(boolean notifications);
    @NotNull BigInteger getTotalTicketsBought();
    void setTotalTicketsBought(@NotNull BigInteger totalTicketsBought);
    @NotNull BigInteger getTotalWins();
    void setTotalWins(@NotNull BigInteger totalWins);
    @NotNull BigDecimal getTotalWonCash();
    void setTotalWonCash(@NotNull BigDecimal totalWonCash);
}
