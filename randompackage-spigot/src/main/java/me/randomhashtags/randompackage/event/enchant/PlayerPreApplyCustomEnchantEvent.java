package me.randomhashtags.randompackage.event.enchant;

import me.randomhashtags.randompackage.addon.CustomEnchant;
import me.randomhashtags.randompackage.event.RPEventCancellable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PlayerPreApplyCustomEnchantEvent extends RPEventCancellable {
	public final CustomEnchant enchant;
	public final int level;
	public PlayerPreApplyCustomEnchantEvent(Player player, CustomEnchant enchant, int level, ItemStack applytoItem) {
		super(player);
		this.enchant = enchant;
		this.level = level;
	}
}