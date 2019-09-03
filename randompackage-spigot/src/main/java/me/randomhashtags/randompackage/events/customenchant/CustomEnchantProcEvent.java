package me.randomhashtags.randompackage.events.customenchant;

import me.randomhashtags.randompackage.addons.CustomEnchant;
import me.randomhashtags.randompackage.events.AbstractCancellable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public class CustomEnchantProcEvent extends AbstractCancellable {
	public final Event event;
	public final CustomEnchant enchant;
	public final int level;
	public final ItemStack itemWithEnchant;
	public boolean didProc;
	public final Player player;
	public CustomEnchantProcEvent(Event event, CustomEnchant enchant, int level, ItemStack itemWithEnchant, Player player) {
		this.event = event;
		this.enchant = enchant;
		this.level = level;
		this.itemWithEnchant = itemWithEnchant;
		this.player = player;
		didProc = false;
	}
}
