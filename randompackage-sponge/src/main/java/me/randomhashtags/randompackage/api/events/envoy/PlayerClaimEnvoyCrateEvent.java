package me.randomhashtags.randompackage.api.events.envoy;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.envoy.LivingEnvoyCrate;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.world.Location;

public class PlayerClaimEnvoyCrateEvent extends RandomPackageEvent implements Cancellable {
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
