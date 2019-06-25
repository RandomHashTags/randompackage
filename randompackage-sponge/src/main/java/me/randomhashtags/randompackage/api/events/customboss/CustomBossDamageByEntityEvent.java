package me.randomhashtags.randompackage.api.events.customboss;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Cancellable;

public class CustomBossDamageByEntityEvent extends RandomPackageEvent implements Cancellable {
	public final Living boss;
	public final Entity damager;
	public double damage;
	private boolean cancelled;
	public CustomBossDamageByEntityEvent(Living boss, Entity damager, double damage) {
		this.boss = boss;
		this.damager = damager;
		this.damage = damage;
	}
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
}