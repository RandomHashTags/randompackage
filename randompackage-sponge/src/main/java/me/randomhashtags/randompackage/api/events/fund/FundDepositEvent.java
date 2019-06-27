package me.randomhashtags.randompackage.api.events.fund;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;

public class FundDepositEvent extends RandomPackageEvent implements Cancellable {
	private static final Fund fund = Fund.getFund();
	public Player player;
	public long amount;
	private final long total;
	private boolean cancelled;
	public FundDepositEvent(Player player, long amount) {
		cancelled = false;
		this.player = player;
		this.amount = amount;
		total = fund.total;
	}
	public long getFundTotal() { return total; }
	public long getNewFundTotal() { return total + amount; }
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
}
