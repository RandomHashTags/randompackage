package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.recode.api.events.AbstractEvent;
import me.randomhashtags.randompackage.recode.api.addons.active.ActivePlayerQuest;
import org.bukkit.entity.Player;

public class PlayerQuestCompleteEvent extends AbstractEvent {
    public final Player player;
    public final ActivePlayerQuest quest;
    public PlayerQuestCompleteEvent(Player player, ActivePlayerQuest quest) {
        this.player = player;
        this.quest = quest;
    }
}
