package me.randomhashtags.randompackage.api.events.globalchallenges;

import me.randomhashtags.randompackage.utils.classes.globalchallenges.GlobalChallenge;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GlobalChallengeBeginEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	public final GlobalChallenge challenge;
	public GlobalChallengeBeginEvent(GlobalChallenge challenge) {
		this.challenge = challenge;
	}
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
}