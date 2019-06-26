package me.randomhashtags.randompackage.api.events.playerquests;

import me.randomhashtags.randompackage.utils.classes.playerquests.ActivePlayerQuest;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PlayerQuestExpireEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    public final UUID player;
    public final ActivePlayerQuest quest;
    public PlayerQuestExpireEvent(UUID player, ActivePlayerQuest quest) {
        this.player = player;
        this.quest = quest;
    }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
