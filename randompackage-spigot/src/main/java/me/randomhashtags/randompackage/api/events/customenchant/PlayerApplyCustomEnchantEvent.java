package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.utils.classes.customenchants.CustomEnchant;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerApplyCustomEnchantEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
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
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
}