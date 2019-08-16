package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.living.ActiveGlobalChallenge;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import java.math.BigDecimal;

public class GlobalChallengeParticipateEvent extends AbstractEvent implements Cancellable {
	public final ActiveGlobalChallenge challenge;
	public final Event event;
	public final String tracked;
	public BigDecimal value;
	private boolean cancelled;
	public GlobalChallengeParticipateEvent(ActiveGlobalChallenge challenge, Event event, String tracked, BigDecimal value) {
		this.challenge = challenge;
		this.event = event;
		this.tracked = tracked;
		this.value = value;
	}
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
}