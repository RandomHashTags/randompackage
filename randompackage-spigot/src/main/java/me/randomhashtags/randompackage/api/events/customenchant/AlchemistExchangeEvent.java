package me.randomhashtags.randompackage.api.events.customenchant;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class AlchemistExchangeEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	public final Player player;
	public final ItemStack one, two, result;
	public final String currency;
	public double price;
	public AlchemistExchangeEvent(Player player, ItemStack one, ItemStack two, String currency, double price, ItemStack result) {
		this.player = player;
		this.one = one;
		this.two = two;
		this.currency = currency;
		this.price = price;
		this.result = result;
	}
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
}