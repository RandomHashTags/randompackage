package me.randomhashtags.randompackage.events.customenchant;

import me.randomhashtags.randompackage.addons.living.LivingCustomEnchantEntity;
import me.randomhashtags.randompackage.events.AbstractCancellable;
import org.bukkit.entity.Entity;

public class CustomEnchantEntityDamageByEntityEvent extends AbstractCancellable {
	public final LivingCustomEnchantEntity entity;
	public final Entity damager;
	public final double finaldamage, initialdamage;
	public CustomEnchantEntityDamageByEntityEvent(LivingCustomEnchantEntity entity, Entity damager, double finaldamage, double initialdamage) {
		this.entity = entity;
		this.damager = damager;
		this.finaldamage = finaldamage;
		this.initialdamage = initialdamage;
	}
	public LivingCustomEnchantEntity getCustomEnchantEntity() { return entity; }
}
