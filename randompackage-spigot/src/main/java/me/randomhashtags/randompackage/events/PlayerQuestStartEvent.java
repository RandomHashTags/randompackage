package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.living.ActivePlayerQuest;
import org.bukkit.event.Cancellable;

import java.util.UUID;

public class PlayerQuestStartEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public final UUID player;
    public final ActivePlayerQuest quest;
    public PlayerQuestStartEvent(UUID player, ActivePlayerQuest quest) {
        this.player = player;
        this.quest = quest;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
}
