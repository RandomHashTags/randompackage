package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.ArmorSet;
import org.bukkit.entity.Player;

public final class ArmorSetUnequipEvent extends RPEvent {
	public final ArmorSet set;
	public ArmorSetUnequipEvent(Player player, ArmorSet set) {
		super(player);
		this.set = set;
	}
}
