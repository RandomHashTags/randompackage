package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.living.ActivePlayerQuest;
import org.bukkit.entity.Player;

public class PlayerQuestCompleteEvent extends AbstractEvent {
    public final Player player;
    public final ActivePlayerQuest quest;
    public PlayerQuestCompleteEvent(Player player, ActivePlayerQuest quest) {
        this.player = player;
        this.quest = quest;
    }
}
