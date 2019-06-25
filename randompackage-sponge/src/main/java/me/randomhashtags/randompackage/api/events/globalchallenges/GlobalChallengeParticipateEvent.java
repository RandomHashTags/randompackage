package me.randomhashtags.randompackage.api.events.globalchallenges;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.globalchallenges.ActiveGlobalChallenge;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.impl.AbstractEvent;

public class GlobalChallengeParticipateEvent extends RandomPackageEvent implements Cancellable {
	public final ActiveGlobalChallenge challenge;
	public final AbstractEvent event;
	public final String tracked;
	public double value;
	private boolean cancelled;
	public GlobalChallengeParticipateEvent(ActiveGlobalChallenge challenge, AbstractEvent event, String tracked, double value) {
		this.challenge = challenge;
		this.event = event;
		this.tracked = tracked;
		this.value = value;
	}
	public boolean isCancelled() { return cancelled; }
	public void setCancelled(boolean cancel) { cancelled = cancel; }
}