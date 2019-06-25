package me.randomhashtags.randompackage.api.events.globalchallenges;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.globalchallenges.ActiveGlobalChallenge;

public class GlobalChallengeEndEvent extends RandomPackageEvent {
	public final ActiveGlobalChallenge challenge;
	public final boolean givesRewards;
	public GlobalChallengeEndEvent(ActiveGlobalChallenge challenge, boolean givesRewards) {
		this.challenge = challenge;
		this.givesRewards = givesRewards;
	}
}
