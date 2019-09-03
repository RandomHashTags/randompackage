package me.randomhashtags.randompackage.events.customenchant;

import me.randomhashtags.randompackage.events.AbstractCancellable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class TinkererTradeEvent extends AbstractCancellable {
	public final Player player;
	public final HashMap<ItemStack, ItemStack> trades;
	public TinkererTradeEvent(Player player, HashMap<ItemStack, ItemStack> trades) {
		this.player = player;
		this.trades = trades;
	}
}