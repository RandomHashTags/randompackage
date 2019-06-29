package me.randomhashtags.randompackage.api.events.envoy;

import me.randomhashtags.randompackage.utils.classes.living.LivingEnvoyCrate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerClaimEnvoyCrateEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	public final Player player;
	public final Location location;
	public final LivingEnvoyCrate type;
	private boolean cancelled;
	public PlayerClaimEnvoyCrateEvent(Player player, Location location, LivingEnvoyCrate type) {
		this.player = player;
		this.location = location;
		this.type = type;
	}
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
}
