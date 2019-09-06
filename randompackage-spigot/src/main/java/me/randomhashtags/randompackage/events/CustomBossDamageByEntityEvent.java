package me.randomhashtags.randompackage.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public class CustomBossDamageByEntityEvent extends DamageEvent {
	private LivingEntity boss;
	public CustomBossDamageByEntityEvent(LivingEntity boss, Entity damager, double damage) {
		super(damager, boss, EntityDamageEvent.DamageCause.CUSTOM, damage);
		this.boss = boss;
	}
	@Override
	public LivingEntity getEntity() { return boss; }
}