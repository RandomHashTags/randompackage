package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.GlobalChallenge;

public final class GlobalChallengeBeginEvent extends AbstractEvent {
	public final GlobalChallenge challenge;
	public GlobalChallengeBeginEvent(GlobalChallenge challenge) {
		this.challenge = challenge;
	}
}