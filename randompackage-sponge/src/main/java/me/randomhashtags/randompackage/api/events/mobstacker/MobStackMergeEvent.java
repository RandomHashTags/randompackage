package me.randomhashtags.randompackage.api.events.mobstacker;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.StackedEntity;
import org.spongepowered.api.event.Cancellable;

public class MobStackMergeEvent extends RandomPackageEvent implements Cancellable {
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
