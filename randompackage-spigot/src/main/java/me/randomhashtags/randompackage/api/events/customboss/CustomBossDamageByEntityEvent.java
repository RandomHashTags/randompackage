package me.randomhashtags.randompackage.api.events.customboss;

import me.randomhashtags.randompackage.recode.api.events.AbstractEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;

public class CustomBossDamageByEntityEvent extends AbstractEvent implements Cancellable {
	public final LivingEntity boss;
	public final Entity damager;
	public double damage;
	private boolean cancelled;
	public CustomBossDamageByEntityEvent(LivingEntity boss, Entity damager, double damage) {
		this.boss = boss;
		this.damager = damager;
		this.damage = damage;
	}
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
}