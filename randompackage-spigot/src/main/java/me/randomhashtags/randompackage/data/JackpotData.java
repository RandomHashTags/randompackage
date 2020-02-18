package me.randomhashtags.randompackage.data;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface JackpotData {
    boolean receivesNotifications();
    void setReceivesNotifications(boolean notifications);
    BigInteger getTotalTicketsBought();
    void setTotalTicketsBought(BigInteger totalTicketsBought);
    BigInteger getTotalWins();
    void setTotalWins(BigInteger totalWins);
    BigDecimal getTotalWonCash();
    void setTotalWonCash(BigDecimal totalWonCash);
}
