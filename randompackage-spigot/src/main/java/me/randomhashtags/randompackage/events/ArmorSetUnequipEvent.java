package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.usingfile.FileArmorSet;
import org.bukkit.entity.Player;

public class ArmorSetUnequipEvent extends AbstractEvent {
	public final Player player;
	public final FileArmorSet set;
	public ArmorSetUnequipEvent(Player player, FileArmorSet set) {
		this.player = player;
		this.set = set;
	}
}
