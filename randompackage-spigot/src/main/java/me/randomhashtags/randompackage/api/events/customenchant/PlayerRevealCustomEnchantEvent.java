package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.api.events.AbstractEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

public class PlayerRevealCustomEnchantEvent extends AbstractEvent implements Cancellable {
	private boolean cancelled;
	public final Player player;
	public final CustomEnchant enchant;
	public final int level;
	public final ItemStack item;
	public PlayerRevealCustomEnchantEvent(Player player, ItemStack item, CustomEnchant enchant, int level) {
		this.player = player;
		this.item = item;
		this.enchant = enchant;
		this.level = level;
	}
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
}