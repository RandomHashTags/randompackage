package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.utils.classes.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class CustomEnchantProcEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	public final Event event;
	public final CustomEnchant enchant;
	public final int level;
	public final ItemStack itemWithEnchant;
	private boolean cancelled;
	public boolean didProc;
	public final Player player;
	public CustomEnchantProcEvent(Event event, CustomEnchant enchant, int level, ItemStack itemWithEnchant, Player player) {
		this.event = event;
		this.enchant = enchant;
		this.level = level;
		this.itemWithEnchant = itemWithEnchant;
		this.player = player;
		cancelled = false;
		didProc = false;
	}
	public void setCancelled(boolean cancel) { cancelled = cancel; }
	public boolean isCancelled() { return cancelled; }
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
}
