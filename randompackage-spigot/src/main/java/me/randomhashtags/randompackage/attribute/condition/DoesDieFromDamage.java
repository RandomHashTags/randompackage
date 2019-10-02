package me.randomhashtags.randompackage.attribute.condition;

import me.randomhashtags.randompackage.attribute.AbstractEventCondition;
import me.randomhashtags.randompackage.event.enchant.isDamagedEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;

public class DoesDieFromDamage extends AbstractEventCondition {
    @Override
    public boolean check(Event event, String value) {
        if(event instanceof isDamagedEvent) {
            final isDamagedEvent e = (isDamagedEvent) event;
            return e.getEntity().getHealth()-e.getDamage() <= 0.00 == Boolean.parseBoolean(value);
        } else if(event instanceof EntityDamageEvent) {
            final EntityDamageEvent e = (EntityDamageEvent) event;
            return e instanceof LivingEntity && ((LivingEntity) e.getEntity()).getHealth()-e.getDamage() <= 0.00 == Boolean.parseBoolean(value);
        }
        return false;
    }
}
