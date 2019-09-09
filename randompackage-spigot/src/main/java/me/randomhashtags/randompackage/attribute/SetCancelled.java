package me.randomhashtags.randompackage.attribute;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class SetCancelled extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value) {
        if(event instanceof Cancellable) {
            ((Cancellable) event).setCancelled(Boolean.parseBoolean(value));
        }
    }
}
