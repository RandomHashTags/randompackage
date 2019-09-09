package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.living.ActivePlayerQuest;
import org.bukkit.entity.Player;

public class PlayerQuestCompleteEvent extends RPEvent {
    public final ActivePlayerQuest quest;
    public PlayerQuestCompleteEvent(Player player, ActivePlayerQuest quest) {
        super(player);
        this.quest = quest;
    }
}
