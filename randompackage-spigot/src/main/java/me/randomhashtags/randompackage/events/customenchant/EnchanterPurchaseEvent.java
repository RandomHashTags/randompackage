package me.randomhashtags.randompackage.events.customenchant;

import me.randomhashtags.randompackage.events.AbstractCancellable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchanterPurchaseEvent extends AbstractCancellable {
	public final Player player;
	public final ItemStack purchased;
	public final String currency;
	public long cost;
	public EnchanterPurchaseEvent(Player player, ItemStack purchased, String currency, long cost) {
		this.player = player;
		this.purchased = purchased;
		this.currency = currency;
		this.cost = cost;
	}
	public Player getPlayer() { return player; }
	public ItemStack getPurchased() { return purchased; }
}