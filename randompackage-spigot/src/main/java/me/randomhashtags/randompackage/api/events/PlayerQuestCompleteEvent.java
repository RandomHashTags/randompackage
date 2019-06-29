package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.utils.abstraction.AbstractEvent;
import me.randomhashtags.randompackage.utils.classes.playerquests.ActivePlayerQuest;
import org.bukkit.entity.Player;

public class PlayerQuestCompleteEvent extends AbstractEvent {
    public final Player player;
    public final ActivePlayerQuest quest;
    public PlayerQuestCompleteEvent(Player player, ActivePlayerQuest quest) {
        this.player = player;
        this.quest = quest;
    }
}
