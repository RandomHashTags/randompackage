package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.living.ActivePlayerQuest;

import java.util.UUID;

public class PlayerQuestStartEvent extends AbstractCancellable {
    public final UUID player;
    public final ActivePlayerQuest quest;
    public PlayerQuestStartEvent(UUID player, ActivePlayerQuest quest) {
        this.player = player;
        this.quest = quest;
    }
}
