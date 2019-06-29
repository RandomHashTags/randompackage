package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.RandomPackage;
import me.randomhashtags.randompackage.utils.abstraction.AbstractEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.HashMap;

public class PlayerTeleportDelayEvent extends AbstractEvent implements Cancellable {
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
		final int t = Bukkit.getScheduler().scheduleSyncDelayedTask(RandomPackage.getPlugin, () -> {
			player.teleport(to, TeleportCause.PLUGIN);
			teleporting.remove(player);
		}, de);
		this.task = t;
		teleporting.put(player, this);
	}

	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
}
