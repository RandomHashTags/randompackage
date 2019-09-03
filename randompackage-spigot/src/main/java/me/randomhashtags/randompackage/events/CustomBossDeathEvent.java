package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.living.LivingCustomBoss;

public class CustomBossDeathEvent extends AbstractCancellable {
	public final LivingCustomBoss boss;
	public CustomBossDeathEvent(LivingCustomBoss boss) {
		this.boss = boss;
	}
}