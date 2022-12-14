package me.randomhashtags.randompackage.event;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class AlchemistExchangeEvent extends RPEventCancellable {
	public final ItemStack one, two, result;
	public final String currency;
	public double price;
	public AlchemistExchangeEvent(Player player, ItemStack one, ItemStack two, String currency, double price, ItemStack result) {
		super(player);
		this.one = one;
		this.two = two;
		this.currency = currency;
		this.price = price;
		this.result = result;
	}
}