package me.randomhashtags.randompackage.event.enchant;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import me.randomhashtags.randompackage.event.RPEventCancellable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class PlayerPreApplyCustomEnchantEvent extends RPEventCancellable {
	public final CustomEnchantSpigot enchant;
	public final int level;
	public PlayerPreApplyCustomEnchantEvent(Player player, CustomEnchantSpigot enchant, int level, ItemStack applytoItem) {
		super(player);
		this.enchant = enchant;
		this.level = level;
	}
}