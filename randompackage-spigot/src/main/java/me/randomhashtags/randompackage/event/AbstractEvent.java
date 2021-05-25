package me.randomhashtags.randompackage.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public abstract class AbstractEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
