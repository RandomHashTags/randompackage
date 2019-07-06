package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.addons.active.ActivePlayerQuest;

import java.util.UUID;

public class PlayerQuestExpireEvent extends AbstractEvent {
    public final UUID player;
    public final ActivePlayerQuest quest;
    public PlayerQuestExpireEvent(UUID player, ActivePlayerQuest quest) {
        this.player = player;
        this.quest = quest;
    }
}
