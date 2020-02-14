package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.addon.PlayerQuest;
import me.randomhashtags.randompackage.addon.living.ActivePlayerQuest;

import java.util.LinkedHashMap;

public interface PlayerQuestData {
    LinkedHashMap<PlayerQuest, ActivePlayerQuest> getQuests();
}
