package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.utils.classes.customenchants.LivingCustomEnchantEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomEnchantEntityDamageByEntityEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	public final LivingCustomEnchantEntity entity;
	public final Entity damager;
	public final double finaldamage, initialdamage;
	private boolean cancelled;
	public CustomEnchantEntityDamageByEntityEvent(LivingCustomEnchantEntity entity, Entity damager, double finaldamage, double initialdamage) {
		this.entity = entity;
		this.damager = damager;
		this.finaldamage = finaldamage;
		this.initialdamage = initialdamage;
	}
	public LivingCustomEnchantEntity getCustomEnchantEntity() { return entity; }
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
}
