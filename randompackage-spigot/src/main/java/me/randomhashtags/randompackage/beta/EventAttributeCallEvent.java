package me.randomhashtags.randompackage.beta;

import me.randomhashtags.randompackage.events.AbstractEvent;
import me.randomhashtags.randompackage.beta.eventattributes.EventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;

public class EventAttributeCallEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public final Entity entity;
    public final EventAttribute attribute;
    public EventAttributeCallEvent(Entity entity, EventAttribute attribute) {
        this.entity = entity;
        this.attribute = attribute;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}