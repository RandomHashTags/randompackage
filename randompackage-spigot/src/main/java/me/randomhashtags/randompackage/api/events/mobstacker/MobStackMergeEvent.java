package me.randomhashtags.randompackage.api.events.mobstacker;

import me.randomhashtags.randompackage.utils.classes.StackedEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobStackMergeEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    public final StackedEntity stackedEntity;
    public final int newSize;
    public MobStackMergeEvent(StackedEntity stackedEntity, int newSize) {
        this.stackedEntity = stackedEntity;
        this.newSize = newSize;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
