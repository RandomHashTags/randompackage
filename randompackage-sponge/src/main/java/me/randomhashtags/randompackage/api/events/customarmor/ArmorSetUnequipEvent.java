package me.randomhashtags.randompackage.api.events.customarmor;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.ArmorSet;
import org.spongepowered.api.entity.living.player.Player;

public class ArmorSetUnequipEvent extends RandomPackageEvent {
	public final Player player;
	public final ArmorSet set;
	public ArmorSetUnequipEvent(Player player, ArmorSet set) {
		this.player = player;
		this.set = set;
	}
}
