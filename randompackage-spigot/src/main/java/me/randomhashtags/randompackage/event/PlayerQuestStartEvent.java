package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.living.ActivePlayerQuest;

import java.util.UUID;

public final class PlayerQuestStartEvent extends AbstractCancellable {
    public final UUID player;
    public final ActivePlayerQuest quest;
    public PlayerQuestStartEvent(UUID player, ActivePlayerQuest quest) {
        this.player = player;
        this.quest = quest;
    }
}
