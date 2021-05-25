package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public final class SetCancelled extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending, String value) {
        final Event event = pending.getEvent();
        if(event instanceof Cancellable) {
            ((Cancellable) event).setCancelled(Boolean.parseBoolean(value));
        }
    }
}
