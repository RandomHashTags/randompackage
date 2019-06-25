package me.randomhashtags.randompackage.api.events.playerquests;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.playerquests.ActivePlayerQuest;
import org.spongepowered.api.entity.living.player.Player;

public class PlayerQuestCompleteEvent extends RandomPackageEvent {
    public final Player player;
    public final ActivePlayerQuest quest;
    public float xp;
    public PlayerQuestCompleteEvent(Player player, ActivePlayerQuest quest) {
        this.player = player;
        this.quest = quest;
    }
}
