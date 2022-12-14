package me.randomhashtags.randompackage.event.mob;

import me.randomhashtags.randompackage.event.DamageEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public class CustomBossDamageByEntityEvent extends DamageEvent {
	private final LivingEntity boss;
	public CustomBossDamageByEntityEvent(@NotNull LivingEntity boss, Entity damager, double damage) {
		super(damager, boss, EntityDamageEvent.DamageCause.CUSTOM, damage);
		this.boss = boss;
	}
	@NotNull
	@Override
	public LivingEntity getEntity() {
		return boss;
	}
}