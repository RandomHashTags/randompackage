package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.living.LivingCustomBoss;

public class CustomBossDeathEvent extends AbstractCancellable {
	public final LivingCustomBoss boss;
	public CustomBossDeathEvent(LivingCustomBoss boss) {
		this.boss = boss;
	}
}