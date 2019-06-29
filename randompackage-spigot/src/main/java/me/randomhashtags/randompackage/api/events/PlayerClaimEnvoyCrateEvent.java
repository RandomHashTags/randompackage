package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.utils.abstraction.AbstractEvent;
import me.randomhashtags.randompackage.utils.classes.living.LivingEnvoyCrate;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class PlayerClaimEnvoyCrateEvent extends AbstractEvent implements Cancellable {
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
}
