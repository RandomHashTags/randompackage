package me.randomhashtags.randompackage.event;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;

public final class PlayerTeleportDelayEvent extends RPEventCancellable {
	public static final HashMap<Player, PlayerTeleportDelayEvent> TELEPORTING = new HashMap<>();
	private double delay;
	private final Location from;
    private Location to;
	private int task;
	
	public PlayerTeleportDelayEvent(Player player, double delay, Location from, Location to) {
		super(player);
		this.delay = delay;
		this.from = from;
		this.to = to;
	}

	public double getDelay() {
		return delay;
	}
	public void setDelay(double delay) {
		this.delay = delay;
	}
	public Location getFrom() {
		return from;
	}
	public Location getTo() {
		return to;
	}
	public void setTo(Location to) {
		this.to = to;
	}

	public int getTask() {
		return task;
	}
	public void setTask(int task) {
		this.task = task;
	}
}
