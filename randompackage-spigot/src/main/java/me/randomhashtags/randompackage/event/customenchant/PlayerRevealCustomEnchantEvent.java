package me.randomhashtags.randompackage.event.customenchant;

import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.event.RPEventCancellable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerRevealCustomEnchantEvent extends RPEventCancellable {
	public final CustomEnchant enchant;
	public final int level;
	public final ItemStack item;
	public PlayerRevealCustomEnchantEvent(Player player, ItemStack item, CustomEnchant enchant, int level) {
		super(player);
		this.item = item;
		this.enchant = enchant;
		this.level = level;
	}
}