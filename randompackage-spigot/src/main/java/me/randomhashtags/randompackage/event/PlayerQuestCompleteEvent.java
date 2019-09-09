package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.living.ActivePlayerQuest;
import org.bukkit.entity.Player;

public class PlayerQuestCompleteEvent extends RPEvent {
    public final ActivePlayerQuest quest;
    public PlayerQuestCompleteEvent(Player player, ActivePlayerQuest quest) {
        super(player);
        this.quest = quest;
    }
}
