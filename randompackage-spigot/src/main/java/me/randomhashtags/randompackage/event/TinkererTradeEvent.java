package me.randomhashtags.randompackage.event;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class TinkererTradeEvent extends RPEventCancellable {
	public final HashMap<ItemStack, ItemStack> trades;
	public TinkererTradeEvent(Player player, HashMap<ItemStack, ItemStack> trades) {
		super(player);
		this.trades = trades;
	}
}