package me.randomhashtags.randompackage.api.events.playerquests;

import me.randomhashtags.randompackage.utils.classes.playerquests.ActivePlayerQuest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerQuestCompleteEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    public final Player player;
    public final ActivePlayerQuest quest;
    public float xp;
    public PlayerQuestCompleteEvent(Player player, ActivePlayerQuest quest) {
        this.player = player;
        this.quest = quest;
    }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
