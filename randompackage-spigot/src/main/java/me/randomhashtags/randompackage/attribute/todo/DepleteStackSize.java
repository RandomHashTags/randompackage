package me.randomhashtags.randompackage.attribute.todo;

import me.randomhashtags.randompackage.attribute.AbstractEventAttribute;
import me.randomhashtags.randompackage.event.MobStackDepleteEvent;
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
