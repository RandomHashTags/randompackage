package me.randomhashtags.randompackage.api.events.customboss;

import me.randomhashtags.randompackage.utils.classes.custombosses.LivingCustomBoss;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CustomBossDeathEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;
	public final LivingCustomBoss boss;
	public CustomBossDeathEvent(LivingCustomBoss boss) {
		this.boss = boss;
	}
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
}