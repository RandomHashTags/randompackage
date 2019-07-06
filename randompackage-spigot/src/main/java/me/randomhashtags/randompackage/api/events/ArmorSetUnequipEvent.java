package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.addons.ArmorSet;
import org.bukkit.entity.Player;

public class ArmorSetUnequipEvent extends AbstractEvent {
	public final Player player;
	public final ArmorSet set;
	public ArmorSetUnequipEvent(Player player, ArmorSet set) {
		this.player = player;
		this.set = set;
	}
}
