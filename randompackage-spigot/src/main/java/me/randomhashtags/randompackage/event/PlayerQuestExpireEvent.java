package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.living.ActivePlayerQuest;

import java.util.UUID;

public class PlayerQuestExpireEvent extends AbstractEvent {
    public final UUID player;
    public final ActivePlayerQuest quest;
    public PlayerQuestExpireEvent(UUID player, ActivePlayerQuest quest) {
        this.player = player;
        this.quest = quest;
    }
}
