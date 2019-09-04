package me.randomhashtags.randompackage.attributes.todo;

import me.randomhashtags.randompackage.attributes.AbstractEventAttribute;
import me.randomhashtags.randompackage.events.MobStackDepleteEvent;
import org.bukkit.event.Event;

public class DepleteStackSize extends AbstractEventAttribute {
    // TODO: finish this attribute / redo mob stack event calling
    @Override
    public void execute(Event event, String value) {
        if(event instanceof MobStackDepleteEvent) {
            final MobStackDepleteEvent e = (MobStackDepleteEvent) event;
            e.amount = (int) evaluate(value);
        }
    }
}
