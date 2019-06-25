package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.customenchants.CustomEnchant;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.item.inventory.ItemStack;

public class PlayerPreApplyCustomEnchantEvent extends RandomPackageEvent implements Cancellable {
	public final Player player;
	public final CustomEnchant enchant;
	public final int level;
	private boolean cancelled;
	public PlayerPreApplyCustomEnchantEvent(Player player, CustomEnchant enchant, int level, ItemStack applytoItem) {
		this.player = player;
		this.enchant = enchant;
		this.level = level;
	}
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
}