package me.randomhashtags.randompackage.addons.objects;

import java.util.List;

public class CustomBossAttack {
	private int chance, radius;
	private List<String> attacks;
	public CustomBossAttack(int chance, int radius, List<String> attacks) {
		this.chance = chance;
		this.radius = radius;
		this.attacks = attacks;
	}
	public int getChance() { return chance; }
	public int getRadius() { return radius; }
	public List<String> getAttacks() { return attacks; }
}
