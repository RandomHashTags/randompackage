package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.addon.GlobalChallengePrize;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface GlobalChallengeData {
    @NotNull
    HashMap<GlobalChallengePrize, Integer> getPrizes();
    void setPrizes(HashMap<GlobalChallengePrize, Integer> prizes);
    default void addPrize(@NotNull GlobalChallengePrize prize) {
        final HashMap<GlobalChallengePrize, Integer> prizes = getPrizes();
        prizes.put(prize, prizes.getOrDefault(prize, 0)+1);
    }
}
