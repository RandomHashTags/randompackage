package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.RandomPackage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.HashMap;

public class PlayerTeleportDelayEvent extends RPEventCancellable {
	public static final HashMap<Player, PlayerTeleportDelayEvent> teleporting = new HashMap<>();
	public final double delay;
	public final Location from, to;
	public final int task;
	
	public PlayerTeleportDelayEvent(Player player, double delay, Location from, Location to) {
		super(player);
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
}
