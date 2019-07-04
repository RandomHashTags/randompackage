package me.randomhashtags.randompackage.events.customenchant;

import me.randomhashtags.randompackage.events.AbstractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class TinkererTradeEvent extends AbstractEvent implements Cancellable {
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