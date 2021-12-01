package me.randomhashtags.randompackage.addon.obj;

import java.util.List;

public final class CustomBossAttack {
	private final int chance, radius;
	private final List<String> attacks;
	public CustomBossAttack(int chance, int radius, List<String> attacks) {
		this.chance = chance;
		this.radius = radius;
		this.attacks = attacks;
	}
	public int getChance() {
		return chance;
	}
	public int getRadius() {
		return radius;
	}
	public List<String> getAttacks() {
		return attacks;
	}
}
