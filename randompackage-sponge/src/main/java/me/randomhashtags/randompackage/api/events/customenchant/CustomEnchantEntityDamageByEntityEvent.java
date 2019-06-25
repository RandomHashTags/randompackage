package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.customenchants.LivingCustomEnchantEntity;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.event.Cancellable;

public class CustomEnchantEntityDamageByEntityEvent extends RandomPackageEvent implements Cancellable {
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
