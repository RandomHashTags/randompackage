package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.living.ActivePlayerQuest;

import java.util.UUID;

public class PlayerQuestExpireEvent extends AbstractEvent {
    public final UUID player;
    public final ActivePlayerQuest quest;
    public PlayerQuestExpireEvent(UUID player, ActivePlayerQuest quest) {
        this.player = player;
        this.quest = quest;
    }
}
