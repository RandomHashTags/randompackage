package me.randomhashtags.randompackage.dev.factions;

import java.math.BigDecimal;

public class FactionBankObj implements FactionBank {
    private BigDecimal bal;
    public FactionBankObj(BigDecimal bal) {
        this.bal = bal;
    }
    public BigDecimal getBalance() { return bal; }
    public void setBalance(BigDecimal bal) { this.bal = bal; }
}
