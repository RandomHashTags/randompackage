package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.addon.GlobalChallengePrize;
import me.randomhashtags.randompackage.data.GlobalChallengeData;

import java.util.HashMap;

public final class GlobalChallengeDataObj implements GlobalChallengeData {
    private HashMap<GlobalChallengePrize, Integer> prizes;

    public GlobalChallengeDataObj(HashMap<GlobalChallengePrize, Integer> prizes) {
        this.prizes = prizes;
    }

    @Override
    public HashMap<GlobalChallengePrize, Integer> getPrizes() {
        return prizes;
    }

    @Override
    public void setPrizes(HashMap<GlobalChallengePrize, Integer> prizes) {
        this.prizes = prizes;
    }
}
