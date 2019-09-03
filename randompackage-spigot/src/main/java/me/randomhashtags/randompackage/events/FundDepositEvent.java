package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.api.Fund;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class FundDepositEvent extends AbstractCancellable {
	private static final Fund fund = Fund.getFund();
	public Player player;
	public BigDecimal amount;
	private final BigDecimal total;
	public FundDepositEvent(Player player, BigDecimal amount) {
		this.player = player;
		this.amount = amount;
		total = fund.total;
	}
	public BigDecimal getFundTotal() { return total; }
	public BigDecimal getNewFundTotal() { return total.add(amount); }
}
