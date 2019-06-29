package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.utils.abstraction.AbstractEvent;
import me.randomhashtags.randompackage.utils.classes.ArmorSet;
import org.bukkit.entity.Player;

public class ArmorSetEquipEvent extends AbstractEvent {
	public final Player player;
	public final ArmorSet set;
	public ArmorSetEquipEvent(Player player, ArmorSet set) {
		this.player = player;
		this.set = set;
	}
}