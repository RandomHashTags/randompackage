package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.addon.PlayerQuest;
import me.randomhashtags.randompackage.addon.living.ActivePlayerQuest;
import me.randomhashtags.randompackage.data.PlayerQuestData;

import java.math.BigInteger;
import java.util.LinkedHashMap;

public final class PlayerQuestDataObj implements PlayerQuestData {
    private BigInteger tokens;
    private LinkedHashMap<PlayerQuest, ActivePlayerQuest> quests;

    public PlayerQuestDataObj(BigInteger tokens, LinkedHashMap<PlayerQuest, ActivePlayerQuest> quests) {
        this.tokens = tokens;
        this.quests = quests;
    }

    @Override
    public BigInteger getTokens() {
        return tokens;
    }
    @Override
    public void setTokens(BigInteger tokens) {
        this.tokens = tokens;
    }

    @Override
    public LinkedHashMap<PlayerQuest, ActivePlayerQuest> getQuests() {
        return quests;
    }
    @Override
    public void setQuests(LinkedHashMap<PlayerQuest, ActivePlayerQuest> quests) {
        this.quests = quests;
    }
}
