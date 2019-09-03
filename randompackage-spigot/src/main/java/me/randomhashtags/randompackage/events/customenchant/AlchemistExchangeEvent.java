package me.randomhashtags.randompackage.events.customenchant;

import me.randomhashtags.randompackage.events.AbstractCancellable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AlchemistExchangeEvent extends AbstractCancellable {
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
}