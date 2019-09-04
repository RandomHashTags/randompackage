package me.randomhashtags.randompackage.attributes.event;

import me.randomhashtags.randompackage.attributes.AbstractEventAttribute;
import me.randomhashtags.randompackage.events.DamageEvent;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

public class SetDamage extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value) {
        if(event instanceof EntityDamageEvent) {
            final EntityDamageEvent e = (EntityDamageEvent) event;
            e.setDamage(evaluate(value.replace("dmg", Double.toString(e.getDamage()))));
        } else if(event instanceof DamageEvent) {
            final DamageEvent e = (DamageEvent) event;
            e.setDamage(evaluate(value.replace("dmg", Double.toString(e.getDamage()))));
        }
    }
}
