package me.randomhashtags.randompackage.utils.attributes.event;

import me.randomhashtags.randompackage.utils.addons.AbstractEventAttribute;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

public class SetDamage extends AbstractEventAttribute {
    @Override
    public void execute(Event event, String value) {
        final EntityDamageEvent e = event instanceof EntityDamageEvent ? (EntityDamageEvent) event : null;
        if(e != null) {
            e.setDamage(evaluate(value.replace("dmg", Double.toString(e.getDamage()))));
        }
    }
}
