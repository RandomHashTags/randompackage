package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.usingfile.FileArmorSet;
import org.bukkit.entity.Player;

public class ArmorSetEquipEvent extends AbstractEvent {
	public final Player player;
	public final FileArmorSet set;
	public ArmorSetEquipEvent(Player player, FileArmorSet set) {
		this.player = player;
		this.set = set;
	}
}