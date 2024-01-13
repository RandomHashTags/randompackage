package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.addon.PlayerQuest;
import me.randomhashtags.randompackage.addon.living.ActivePlayerQuest;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.LinkedHashMap;

public interface PlayerQuestData {
    @NotNull BigInteger getTokens();
    void setTokens(@NotNull BigInteger tokens);
    @NotNull LinkedHashMap<PlayerQuest, ActivePlayerQuest> getQuests();
    void setQuests(@NotNull LinkedHashMap<PlayerQuest, ActivePlayerQuest> quests);
}
