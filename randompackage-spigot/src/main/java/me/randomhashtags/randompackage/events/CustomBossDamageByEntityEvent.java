package me.randomhashtags.randompackage.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class CustomBossDamageByEntityEvent extends DamageEvent {
	public final LivingEntity boss;
	public final Entity damager;
	public CustomBossDamageByEntityEvent(LivingEntity boss, Entity damager, double damage) {
		this.boss = boss;
		this.damager = damager;
		setDamage(damage);
	}
}