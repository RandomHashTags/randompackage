package me.randomhashtags.randompackage.events.customenchant;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

public class EnchanterPurchaseEvent extends AbstractEvent implements Cancellable {
	public final Player player;
	public final ItemStack purchased;
	public final String currency;
	public long cost;
	private boolean cancelled;
	public EnchanterPurchaseEvent(Player player, ItemStack purchased, String currency, long cost) {
		this.player = player;
		this.purchased = purchased;
		this.currency = currency;
		this.cost = cost;
	}
	public Player getPlayer() { return player; }
	public ItemStack getPurchased() { return purchased; }
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
}