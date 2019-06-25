package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.item.inventory.ItemStack;

public class EnchanterPurchaseEvent extends RandomPackageEvent implements Cancellable {
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