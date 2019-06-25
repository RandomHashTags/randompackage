package me.randomhashtags.randompackage.api.events.customarmor;

import me.randomhashtags.randompackage.utils.classes.ArmorSet;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArmorSetUnequipEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	public final Player player;
	public final ArmorSet set;
	public ArmorSetUnequipEvent(Player player, ArmorSet set) {
		this.player = player;
		this.set = set;
	}
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
}
