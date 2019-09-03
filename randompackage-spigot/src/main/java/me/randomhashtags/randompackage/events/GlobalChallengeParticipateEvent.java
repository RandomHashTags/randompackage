package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.living.ActiveGlobalChallenge;
import org.bukkit.event.Event;

import java.math.BigDecimal;

public class GlobalChallengeParticipateEvent extends AbstractCancellable {
	public final ActiveGlobalChallenge challenge;
	public final Event event;
	public final String tracked;
	public BigDecimal value;
	public GlobalChallengeParticipateEvent(ActiveGlobalChallenge challenge, Event event, String tracked, BigDecimal value) {
		this.challenge = challenge;
		this.event = event;
		this.tracked = tracked;
		this.value = value;
	}
}