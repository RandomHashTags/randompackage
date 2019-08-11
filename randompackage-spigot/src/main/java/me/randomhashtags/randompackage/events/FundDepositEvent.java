package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.api.Fund;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

import java.math.BigDecimal;

public class FundDepositEvent extends AbstractEvent implements Cancellable {
	private static final Fund fund = Fund.getFund();
	public Player player;
	public BigDecimal amount;
	private final BigDecimal total;
	private boolean cancelled;
	public FundDepositEvent(Player player, BigDecimal amount) {
		cancelled = false;
		this.player = player;
		this.amount = amount;
		total = fund.total;
	}
	public BigDecimal getFundTotal() { return total; }
	public BigDecimal getNewFundTotal() { return total.add(amount); }
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
}
