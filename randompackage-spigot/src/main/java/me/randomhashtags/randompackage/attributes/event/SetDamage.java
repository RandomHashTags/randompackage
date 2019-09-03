package me.randomhashtags.randompackage.attributes.event;

import me.randomhashtags.randompackage.attributes.AbstractEventAttribute;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

public class SetDamage extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value) {
        final EntityDamageEvent e = event instanceof EntityDamageEvent ? (EntityDamageEvent) event : null;
        if(e != null) {
            e.setDamage(evaluate(value));
        }
    }
}
