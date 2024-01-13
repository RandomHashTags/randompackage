package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.addon.PlayerQuest;
import me.randomhashtags.randompackage.addon.living.ActivePlayerQuest;
import me.randomhashtags.randompackage.data.PlayerQuestData;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.LinkedHashMap;

public final class PlayerQuestDataObj implements PlayerQuestData {
    private BigInteger tokens;
    private LinkedHashMap<PlayerQuest, ActivePlayerQuest> quests;

    public PlayerQuestDataObj(@NotNull BigInteger tokens, @NotNull LinkedHashMap<PlayerQuest, ActivePlayerQuest> quests) {
        this.tokens = tokens;
        this.quests = quests;
    }

    @Override
    public @NotNull BigInteger getTokens() {
        return tokens;
    }
    @Override
    public void setTokens(@NotNull BigInteger tokens) {
        this.tokens = tokens;
    }

    @Override
    public @NotNull LinkedHashMap<PlayerQuest, ActivePlayerQuest> getQuests() {
        return quests;
    }
    @Override
    public void setQuests(@NotNull LinkedHashMap<PlayerQuest, ActivePlayerQuest> quests) {
        this.quests = quests;
    }
}
