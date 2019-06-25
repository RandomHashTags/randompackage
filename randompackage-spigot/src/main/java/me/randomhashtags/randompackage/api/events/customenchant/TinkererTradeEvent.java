package me.randomhashtags.randompackage.api.events.customenchant;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class TinkererTradeEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	public final Player player;
	public final HashMap<ItemStack, ItemStack> trades;
	private boolean cancelled;
	public TinkererTradeEvent(Player player, HashMap<ItemStack, ItemStack> trades) {
		this.player = player;
		this.trades = trades;
	}
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
}