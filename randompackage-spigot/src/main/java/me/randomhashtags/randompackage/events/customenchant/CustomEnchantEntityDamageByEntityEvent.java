package me.randomhashtags.randompackage.events.customenchant;

import me.randomhashtags.randompackage.addons.active.LivingCustomEnchantEntity;
import me.randomhashtags.randompackage.events.AbstractEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;

public class CustomEnchantEntityDamageByEntityEvent extends AbstractEvent implements Cancellable {
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
}
