package me.randomhashtags.randompackage.utils.supported;

import java.math.BigDecimal;
import java.util.UUID;

public interface Economical {
    void set(UUID player, BigDecimal amount);
    void deposit(UUID player, BigDecimal amount);
    void withdraw(UUID player, BigDecimal amount);

    void transactionSuccessful(UUID player, BigDecimal amount, boolean isWithdraw);
}
