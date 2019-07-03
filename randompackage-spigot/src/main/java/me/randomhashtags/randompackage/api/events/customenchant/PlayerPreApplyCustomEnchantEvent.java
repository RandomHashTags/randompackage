package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.recode.api.events.AbstractEvent;
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