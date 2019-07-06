package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.addons.enums.CustomEnchantApplyResult;
import me.randomhashtags.randompackage.api.events.AbstractEvent;
import org.bukkit.entity.Player;

public class PlayerApplyCustomEnchantEvent extends AbstractEvent {
	public final Player player;
	public final CustomEnchant enchant;
	public final int level;
	public final CustomEnchantApplyResult result;
	public PlayerApplyCustomEnchantEvent(Player player, CustomEnchant enchant, int level, CustomEnchantApplyResult result) {
		this.player = player;
		this.enchant = enchant;
		this.level = level;
		this.result = result;
	}
}