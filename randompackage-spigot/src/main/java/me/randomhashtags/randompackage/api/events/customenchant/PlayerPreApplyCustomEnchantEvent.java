package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.utils.classes.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerPreApplyCustomEnchantEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
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
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
}