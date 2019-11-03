package me.randomhashtags.randompackage.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import java.util.HashMap;

public class Heal extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<String, Entity> entities, HashMap<Entity, String> recipientValues, HashMap<String, String> valueReplacements) {
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof LivingEntity) {
                final LivingEntity l = (LivingEntity) e;
                final String value = replaceValue(entities, recipientValues.get(e).replace("hp", Double.toString(l.getHealth())), valueReplacements);
                final double total = evaluate(value);
                l.setHealth(Math.min(l.getMaxHealth(), total+l.getHealth()));
            }
        }
    }
}
