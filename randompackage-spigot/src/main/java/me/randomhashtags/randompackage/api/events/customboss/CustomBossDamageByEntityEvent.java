package me.randomhashtags.randompackage.api.events.customboss;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomBossDamageByEntityEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
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
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
}