package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.recode.api.events.AbstractEvent;
import me.randomhashtags.randompackage.recode.api.addons.active.ActiveGlobalChallenge;

public class GlobalChallengeEndEvent extends AbstractEvent {
	public final ActiveGlobalChallenge challenge;
	public final boolean givesRewards;
	public GlobalChallengeEndEvent(ActiveGlobalChallenge challenge, boolean givesRewards) {
		this.challenge = challenge;
		this.givesRewards = givesRewards;
	}
}
