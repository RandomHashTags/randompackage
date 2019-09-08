package me.randomhashtags.randompackage.attributes;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

public class SetDamage extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value) {
        if(event instanceof EntityDamageEvent) {
            final EntityDamageEvent e = (EntityDamageEvent) event;
            e.setDamage(evaluate(value));
        }
    }
}