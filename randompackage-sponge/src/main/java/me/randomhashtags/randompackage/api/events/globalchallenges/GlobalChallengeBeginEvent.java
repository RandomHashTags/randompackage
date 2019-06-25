package me.randomhashtags.randompackage.api.events.globalchallenges;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.globalchallenges.GlobalChallenge;

public class GlobalChallengeBeginEvent extends RandomPackageEvent {
	public final GlobalChallenge challenge;
	public GlobalChallengeBeginEvent(GlobalChallenge challenge) {
		this.challenge = challenge;
	}
}