package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.objects.StackedEntity;
import org.bukkit.event.Cancellable;

public class MobStackMergeEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public final StackedEntity stackedEntity;
    public final int newSize;
    public MobStackMergeEvent(StackedEntity stackedEntity, int newSize) {
        this.stackedEntity = stackedEntity;
        this.newSize = newSize;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
}