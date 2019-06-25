package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.customenchants.CustomEnchant;
import org.spongepowered.api.entity.living.player.Player;

public class PlayerApplyCustomEnchantEvent extends RandomPackageEvent {
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