package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.customenchants.CustomEnchant;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.item.inventory.ItemStack;

public class CustomEnchantProcEvent extends RandomPackageEvent implements Cancellable {
	public final AbstractEvent event;
	public final CustomEnchant enchant;
	public final int level;
	public final ItemStack itemWithEnchant;
	private boolean cancelled;
	public boolean didProc;
	public final Player player;
	public CustomEnchantProcEvent(AbstractEvent event, CustomEnchant enchant, int level, ItemStack itemWithEnchant, Player player) {
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
}
