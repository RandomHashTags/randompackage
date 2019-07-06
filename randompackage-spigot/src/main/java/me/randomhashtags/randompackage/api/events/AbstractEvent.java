package me.randomhashtags.randompackage.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AbstractEvent extends Event {
    private HandlerList handlers = new HandlerList();
    public HandlerList getHandlers() { return handlers; }
}
