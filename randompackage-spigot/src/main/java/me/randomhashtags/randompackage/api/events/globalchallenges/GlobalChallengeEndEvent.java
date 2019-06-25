package me.randomhashtags.randompackage.api.events.globalchallenges;

import me.randomhashtags.randompackage.utils.classes.globalchallenges.ActiveGlobalChallenge;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GlobalChallengeEndEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
	public final ActiveGlobalChallenge challenge;
	public final boolean givesRewards;
	public GlobalChallengeEndEvent(ActiveGlobalChallenge challenge, boolean givesRewards) {
		this.challenge = challenge;
		this.givesRewards = givesRewards;
	}
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
}
