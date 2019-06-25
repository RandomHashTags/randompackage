package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.RandomPackage;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.world.Location;

import java.util.HashMap;

public class PlayerTeleportDelayEvent extends RandomPackageEvent implements Cancellable {
	public static final HashMap<Player, PlayerTeleportDelayEvent> teleporting = new HashMap<>();
	private boolean cancelled;
	public final Player player;
	public final double delay;
	public final Location from, to;
	public final int task;
	
	public PlayerTeleportDelayEvent(Player player, double delay, Location from, Location to) {
		cancelled = false;
		this.player = player;
		this.delay = delay;
		this.from = from;
		this.to = to;
		final long de = (long) ((((long) delay * 20)) + (20 * Double.parseDouble("0." + Double.toString(delay).split("\\.")[1])));
		final int t = Sponge.getScheduler().createTaskBuilder().delayTicks(de).execute(() -> {
			player.setLocation(to, TeleportCause.PLUGIN);
			teleporting.remove(player);
		});
		this.task = t;
		teleporting.put(player, this);
	}

	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
}
