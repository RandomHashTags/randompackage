package me.randomhashtags.randompackage.api.events.fund;

import me.randomhashtags.randompackage.api.Fund;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FundDepositEvent extends Event implements Cancellable {
	private static final Fund fund = Fund.getFund();
	public Player player;
	public long amount;
	private final long total;
	private static final HandlerList handlers = new HandlerList();
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
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
}
