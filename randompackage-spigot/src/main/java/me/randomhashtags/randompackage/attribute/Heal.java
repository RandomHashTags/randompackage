package me.randomhashtags.randompackage.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class Heal extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof LivingEntity) {
                final LivingEntity l = (LivingEntity) e;
                final String value = recipientValues.get(e).replace("hp", Double.toString(l.getHealth()));
                final double total = evaluate(value);
                l.setHealth(Math.min(l.getMaxHealth(), total));
            }
        }
    }
}
