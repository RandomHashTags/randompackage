package me.randomhashtags.randompackage.events.customboss;

import me.randomhashtags.randompackage.addons.active.LivingCustomBoss;
import org.bukkit.event.Cancellable;

public class CustomBossDeathEvent extends AbstractEvent implements Cancellable {
	private boolean cancelled;
	public final LivingCustomBoss boss;
	public CustomBossDeathEvent(LivingCustomBoss boss) {
		this.boss = boss;
	}
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
}