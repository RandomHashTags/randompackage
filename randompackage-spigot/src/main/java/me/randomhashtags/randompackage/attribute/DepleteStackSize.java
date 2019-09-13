package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.event.MobStackDepleteEvent;
import org.bukkit.event.Event;

public class DepleteStackSize extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value) {
        if(event instanceof MobStackDepleteEvent) {
            final MobStackDepleteEvent e = (MobStackDepleteEvent) event;
            e.amount = (int) evaluate(value);
        }
    }
}
