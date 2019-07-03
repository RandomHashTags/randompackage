package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.recode.api.events.AbstractEvent;
import me.randomhashtags.randompackage.recode.api.addons.usingFile.FileArmorSet;
import org.bukkit.entity.Player;

public class ArmorSetEquipEvent extends AbstractEvent {
	public final Player player;
	public final FileArmorSet set;
	public ArmorSetEquipEvent(Player player, FileArmorSet set) {
		this.player = player;
		this.set = set;
	}
}