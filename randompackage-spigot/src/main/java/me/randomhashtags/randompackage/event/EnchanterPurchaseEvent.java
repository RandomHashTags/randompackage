package me.randomhashtags.randompackage.event;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class EnchanterPurchaseEvent extends RPEventCancellable {
	public final ItemStack purchased;
	public final String currency;
	public long cost;
	public EnchanterPurchaseEvent(Player player, ItemStack purchased, String currency, long cost) {
		super(player);
		this.purchased = purchased;
		this.currency = currency;
		this.cost = cost;
	}
	public ItemStack getPurchased() {
		return purchased;
	}
}