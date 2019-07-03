package me.randomhashtags.randompackage.api.events.customboss;

import me.randomhashtags.randompackage.recode.api.events.AbstractEvent;
import me.randomhashtags.randompackage.recode.api.addons.active.LivingCustomBoss;
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