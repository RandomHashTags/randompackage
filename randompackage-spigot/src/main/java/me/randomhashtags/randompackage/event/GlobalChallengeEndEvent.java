package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.living.ActiveGlobalChallenge;

public class GlobalChallengeEndEvent extends AbstractEvent {
	public final ActiveGlobalChallenge challenge;
	public final boolean givesRewards;
	public GlobalChallengeEndEvent(ActiveGlobalChallenge challenge, boolean givesRewards) {
		this.challenge = challenge;
		this.givesRewards = givesRewards;
	}
}
