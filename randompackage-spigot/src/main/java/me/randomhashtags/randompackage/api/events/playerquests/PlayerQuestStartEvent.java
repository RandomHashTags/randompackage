package me.randomhashtags.randompackage.api.events.playerquests;

import me.randomhashtags.randompackage.utils.classes.playerquests.ActivePlayerQuest;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PlayerQuestStartEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    public final UUID player;
    public final ActivePlayerQuest quest;
    public PlayerQuestStartEvent(UUID player, ActivePlayerQuest quest) {
        this.player = player;
        this.quest = quest;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
