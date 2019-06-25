package me.randomhashtags.randompackage.api.events.globalchallenges;

import me.randomhashtags.randompackage.utils.classes.globalchallenges.ActiveGlobalChallenge;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GlobalChallengeParticipateEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	public final ActiveGlobalChallenge challenge;
	public final Event event;
	public final String tracked;
	public double value;
	private boolean cancelled;
	public GlobalChallengeParticipateEvent(ActiveGlobalChallenge challenge, Event event, String tracked, double value) {
		this.challenge = challenge;
		this.event = event;
		this.tracked = tracked;
		this.value = value;
	}
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
}