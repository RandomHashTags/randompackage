package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.customenchants.CustomEnchant;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.item.inventory.ItemStack;

public class PlayerRevealCustomEnchantEvent extends RandomPackageEvent implements Cancellable {
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