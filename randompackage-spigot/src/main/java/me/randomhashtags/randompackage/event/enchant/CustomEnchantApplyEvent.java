package me.randomhashtags.randompackage.event.enchant;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import me.randomhashtags.randompackage.event.RPEvent;
import org.bukkit.entity.Player;

public class CustomEnchantApplyEvent extends RPEvent {
	public final CustomEnchantSpigot enchant;
	public final int level, success, destroy;
	public final String result;
	public CustomEnchantApplyEvent(Player player, CustomEnchantSpigot enchant, int level, int success, int destroy, String result) {
		super(player);
		this.enchant = enchant;
		this.level = level;
		this.success = success;
		this.destroy = destroy;
		this.result = result;
	}
}