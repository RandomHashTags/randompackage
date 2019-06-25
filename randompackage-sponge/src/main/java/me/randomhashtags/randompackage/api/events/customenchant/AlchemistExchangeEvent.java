package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.item.inventory.ItemStack;

public class AlchemistExchangeEvent extends RandomPackageEvent implements Cancellable {
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
}