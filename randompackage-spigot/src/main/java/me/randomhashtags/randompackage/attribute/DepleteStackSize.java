package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import me.randomhashtags.randompackage.event.mob.MobStackDepleteEvent;
import org.bukkit.event.Event;

public class DepleteStackSize extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending, String value) {
        final Event event = pending.getEvent();
        if(event instanceof MobStackDepleteEvent) {
            final MobStackDepleteEvent e = (MobStackDepleteEvent) event;
            e.amount = (int) evaluate(value);
        }
    }
}
