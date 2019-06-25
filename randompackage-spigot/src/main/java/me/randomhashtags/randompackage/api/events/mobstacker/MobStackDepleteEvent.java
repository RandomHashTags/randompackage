package me.randomhashtags.randompackage.api.events.mobstacker;

import me.randomhashtags.randompackage.utils.classes.StackedEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MobStackDepleteEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    public final StackedEntity stack;
    public final Entity killer;
    public int amount;
    public MobStackDepleteEvent(StackedEntity stack, Entity killer, int amount) {
        this.stack = stack;
        this.killer = killer;
        this.amount = amount;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
