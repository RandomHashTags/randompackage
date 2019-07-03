package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.recode.api.events.AbstractEvent;
import me.randomhashtags.randompackage.utils.enums.CustomEnchantApplyResult;
import org.bukkit.entity.Player;

public class PlayerApplyCustomEnchantEvent extends AbstractEvent {
	public final Player player;
	public final AbstractCustomEnchant enchant;
	public final int level;
	public final CustomEnchantApplyResult result;
	public PlayerApplyCustomEnchantEvent(Player player, AbstractCustomEnchant enchant, int level, CustomEnchantApplyResult result) {
		this.player = player;
		this.enchant = enchant;
		this.level = level;
		this.result = result;
	}
}