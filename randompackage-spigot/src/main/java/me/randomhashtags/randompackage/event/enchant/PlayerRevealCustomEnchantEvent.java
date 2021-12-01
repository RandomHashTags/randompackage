package me.randomhashtags.randompackage.event.enchant;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import me.randomhashtags.randompackage.event.RPEventCancellable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerRevealCustomEnchantEvent extends RPEventCancellable {
	public final CustomEnchantSpigot enchant;
	public final int level;
	public final ItemStack item;
	public PlayerRevealCustomEnchantEvent(Player player, ItemStack item, CustomEnchantSpigot enchant, int level) {
		super(player);
		this.item = item;
		this.enchant = enchant;
		this.level = level;
	}
}