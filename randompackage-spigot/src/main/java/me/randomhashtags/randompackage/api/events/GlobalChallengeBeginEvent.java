package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.addons.GlobalChallenge;

public class GlobalChallengeBeginEvent extends AbstractEvent {
	public final GlobalChallenge challenge;
	public GlobalChallengeBeginEvent(GlobalChallenge challenge) {
		this.challenge = challenge;
	}
}