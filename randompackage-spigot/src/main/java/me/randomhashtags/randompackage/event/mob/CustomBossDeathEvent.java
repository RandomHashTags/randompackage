package me.randomhashtags.randompackage.event.mob;

import me.randomhashtags.randompackage.addon.living.LivingCustomBoss;
import me.randomhashtags.randompackage.event.AbstractCancellable;

public class CustomBossDeathEvent extends AbstractCancellable {
	public final LivingCustomBoss boss;
	public CustomBossDeathEvent(LivingCustomBoss boss) {
		this.boss = boss;
	}
}