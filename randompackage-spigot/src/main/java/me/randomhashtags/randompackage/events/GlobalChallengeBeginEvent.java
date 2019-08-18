package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.legacy.GlobalChallenge;

public class GlobalChallengeBeginEvent extends AbstractEvent {
	public final GlobalChallenge challenge;
	public GlobalChallengeBeginEvent(GlobalChallenge challenge) {
		this.challenge = challenge;
	}
}