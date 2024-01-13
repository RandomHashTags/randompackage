package me.randomhashtags.randompackage.event.enchant;

import me.randomhashtags.randompackage.addon.CustomEnchantSpigot;
import me.randomhashtags.randompackage.event.RPEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CustomEnchantApplyEvent extends RPEvent {
	@NotNull public final CustomEnchantSpigot enchant;
	public final int level, success, destroy;
	@NotNull public final String result;
	public CustomEnchantApplyEvent(@NotNull Player player, @NotNull CustomEnchantSpigot enchant, int level, int success, int destroy, @NotNull String result) {
		super(player);
		this.enchant = enchant;
		this.level = level;
		this.success = success;
		this.destroy = destroy;
		this.result = result;
	}
}