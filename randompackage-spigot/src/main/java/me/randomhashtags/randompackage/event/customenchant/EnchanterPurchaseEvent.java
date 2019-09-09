package me.randomhashtags.randompackage.event.customenchant;

import me.randomhashtags.randompackage.event.RPEventCancellable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchanterPurchaseEvent extends RPEventCancellable {
	public final ItemStack purchased;
	public final String currency;
	public long cost;
	public EnchanterPurchaseEvent(Player player, ItemStack purchased, String currency, long cost) {
		super(player);
		this.purchased = purchased;
		this.currency = currency;
		this.cost = cost;
	}
	public ItemStack getPurchased() { return purchased; }
}