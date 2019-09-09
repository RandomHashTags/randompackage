package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.ArmorSet;
import org.bukkit.entity.Player;

public class ArmorSetEquipEvent extends RPEvent {
	public final ArmorSet set;
	public ArmorSetEquipEvent(Player player, ArmorSet set) {
		super(player);
		this.set = set;
	}
}