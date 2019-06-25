package me.randomhashtags.randompackage.api.events.customboss;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.custombosses.LivingCustomBoss;
import org.spongepowered.api.event.Cancellable;

public class CustomBossDeathEvent extends RandomPackageEvent implements Cancellable {
	private boolean cancelled;
	public final LivingCustomBoss boss;
	public CustomBossDeathEvent(LivingCustomBoss boss) {
		this.boss = boss;
	}
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
}