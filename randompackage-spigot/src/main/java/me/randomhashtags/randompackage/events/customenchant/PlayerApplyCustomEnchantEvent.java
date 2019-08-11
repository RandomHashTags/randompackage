package me.randomhashtags.randompackage.events.customenchant;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.events.AbstractEvent;
import org.bukkit.entity.Player;

public class PlayerApplyCustomEnchantEvent extends AbstractEvent {
	public final Player player;
	public final CustomEnchant enchant;
	public final int level;
	public final String result;
	public PlayerApplyCustomEnchantEvent(Player player, CustomEnchant enchant, int level, String result) {
		this.player = player;
		this.enchant = enchant;
		this.level = level;
		this.result = result;
	}
}