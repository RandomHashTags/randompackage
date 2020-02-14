package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.addon.GlobalChallengePrize;

import java.util.HashMap;

public interface GlobalChallengeData {
    HashMap<GlobalChallengePrize, Integer> getPrizes();
}
