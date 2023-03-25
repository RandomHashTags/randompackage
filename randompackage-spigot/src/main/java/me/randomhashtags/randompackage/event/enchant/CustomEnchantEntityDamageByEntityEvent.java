package me.randomhashtags.randompackage.event.enchant;

import me.randomhashtags.randompackage.addon.living.LivingCustomEnchantEntity;
import me.randomhashtags.randompackage.event.AbstractCancellable;
import org.bukkit.entity.Entity;

public class CustomEnchantEntityDamageByEntityEvent extends AbstractCancellable {
	public final LivingCustomEnchantEntity entity;
	public final Entity damager;
	public final double final_damage, initial_damage;
	public CustomEnchantEntityDamageByEntityEvent(LivingCustomEnchantEntity entity, Entity damager, double final_damage, double initial_damage) {
		this.entity = entity;
		this.damager = damager;
		this.final_damage = final_damage;
		this.initial_damage = initial_damage;
	}
}
