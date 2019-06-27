package me.randomhashtags.randompackage.api.events.playerquests;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.playerquests.ActivePlayerQuest;

import java.util.UUID;

public class PlayerQuestExpireEvent extends RandomPackageEvent {
    public final UUID player;
    public final ActivePlayerQuest quest;
    public PlayerQuestExpireEvent(UUID player, ActivePlayerQuest quest) {
        this.player = player;
        this.quest = quest;
    }
}
