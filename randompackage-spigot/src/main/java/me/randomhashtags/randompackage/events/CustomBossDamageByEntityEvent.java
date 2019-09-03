package me.randomhashtags.randompackage.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class CustomBossDamageByEntityEvent extends AbstractCancellable {
	public final LivingEntity boss;
	public final Entity damager;
	public double damage;
	public CustomBossDamageByEntityEvent(LivingEntity boss, Entity damager, double damage) {
		this.boss = boss;
		this.damager = damager;
		this.damage = damage;
	}
}