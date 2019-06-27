package me.randomhashtags.randompackage.api.events.playerquests;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.playerquests.ActivePlayerQuest;
import org.spongepowered.api.event.Cancellable;

import java.util.UUID;

public class PlayerQuestStartEvent extends RandomPackageEvent implements Cancellable {
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
