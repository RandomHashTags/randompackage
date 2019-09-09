package me.randomhashtags.randompackage.events.customenchant;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.events.AbstractCancellable;
import me.randomhashtags.randompackage.events.RPEventCancellable;
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