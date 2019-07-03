package me.randomhashtags.randompackage.events.customenchant;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

public class PlayerPreApplyCustomEnchantEvent extends AbstractEvent implements Cancellable {
	public final Player player;
	public final AbstractCustomEnchant enchant;
	public final int level;
	private boolean cancelled;
	public PlayerPreApplyCustomEnchantEvent(Player player, AbstractCustomEnchant enchant, int level, ItemStack applytoItem) {
		this.player = player;
		this.enchant = enchant;
		this.level = level;
	}
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
}