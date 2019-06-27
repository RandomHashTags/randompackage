package me.randomhashtags.randompackage.utils.classes.globalchallenges;

import me.randomhashtags.randompackage.utils.abstraction.AbstractGlobalChallenge;

import java.io.File;
import java.util.*;

public class GlobalChallenge extends AbstractGlobalChallenge {
	public static TreeMap<String, GlobalChallenge> challenges;

	public long started;
	public GlobalChallenge(File f, Set<UUID> participants) {
		if(challenges == null) challenges = new TreeMap<>();
		load(f);
		setParticipants(participants);
		challenges.put(getYamlName(), this);
	}
	public ActiveGlobalChallenge start() {
		return start(System.currentTimeMillis(), new HashMap<>());
	}
	public ActiveGlobalChallenge start(long started, HashMap<UUID, Double> participants) {
		final HashMap<GlobalChallenge, ActiveGlobalChallenge> a = ActiveGlobalChallenge.active;
		return a != null ? a.getOrDefault(this, new ActiveGlobalChallenge(started, this, participants)) : new ActiveGlobalChallenge(started, this, participants);
	}
	public boolean isActive() {
		final HashMap<GlobalChallenge, ActiveGlobalChallenge> a = ActiveGlobalChallenge.active;
		return a != null && a.containsKey(this);
	}

	public static void deleteAll() {
		challenges = null;
	}
}
