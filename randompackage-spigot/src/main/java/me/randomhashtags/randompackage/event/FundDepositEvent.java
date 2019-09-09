package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.api.Fund;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class FundDepositEvent extends RPEventCancellable {
	private static final Fund fund = Fund.getFund();
	public BigDecimal amount;
	private final BigDecimal total;
	public FundDepositEvent(Player player, BigDecimal amount) {
		super(player);
		this.amount = amount;
		total = fund.total;
	}
	public BigDecimal getFundTotal() { return total; }
	public BigDecimal getNewFundTotal() { return total.add(amount); }
}
