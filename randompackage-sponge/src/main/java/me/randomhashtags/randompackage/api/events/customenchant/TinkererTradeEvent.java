package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.HashMap;

public class TinkererTradeEvent extends RandomPackageEvent implements Cancellable {
	public final Player player;
	public final HashMap<ItemStack, ItemStack> trades;
	private boolean cancelled;
	public TinkererTradeEvent(Player player, HashMap<ItemStack, ItemStack> trades) {
		this.player = player;
		this.trades = trades;
	}
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
}