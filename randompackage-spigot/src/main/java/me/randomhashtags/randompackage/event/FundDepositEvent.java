package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.api.Fund;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public final class FundDepositEvent extends RPEventCancellable {
	public BigDecimal amount;
	private final BigDecimal total;
	public FundDepositEvent(Player player, BigDecimal amount) {
		super(player);
		this.amount = amount;
		total = Fund.INSTANCE.total;
	}
	public BigDecimal getFundTotal() {
		return total;
	}
	public BigDecimal getNewFundTotal() {
		return total.add(amount);
	}
}
