package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.addon.PlayerQuest;
import me.randomhashtags.randompackage.addon.living.ActivePlayerQuest;
import me.randomhashtags.randompackage.data.PlayerQuestData;

import java.util.LinkedHashMap;

public class PlayerQuestDataObj implements PlayerQuestData {
    private LinkedHashMap<PlayerQuest, ActivePlayerQuest> quests;

    public PlayerQuestDataObj(LinkedHashMap<PlayerQuest, ActivePlayerQuest> quests) {
        this.quests = quests;
    }

    @Override
    public LinkedHashMap<PlayerQuest, ActivePlayerQuest> getQuests() {
        return quests;
    }
}
