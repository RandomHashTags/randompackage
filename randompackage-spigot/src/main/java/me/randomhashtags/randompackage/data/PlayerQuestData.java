package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.addon.PlayerQuest;
import me.randomhashtags.randompackage.addon.living.ActivePlayerQuest;

import java.math.BigInteger;
import java.util.LinkedHashMap;

public interface PlayerQuestData {
    BigInteger getTokens();
    void setTokens(BigInteger tokens);
    LinkedHashMap<PlayerQuest, ActivePlayerQuest> getQuests();
    void setQuests(LinkedHashMap<PlayerQuest, ActivePlayerQuest> quests);
}
