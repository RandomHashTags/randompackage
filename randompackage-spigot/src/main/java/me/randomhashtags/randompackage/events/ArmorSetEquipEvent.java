package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.ArmorSet;
import org.bukkit.entity.Player;

public class ArmorSetEquipEvent extends AbstractEvent {
	public final Player player;
	public final ArmorSet set;
	public ArmorSetEquipEvent(Player player, ArmorSet set) {
		this.player = player;
		this.set = set;
	}
}