package me.randomhashtags.randompackage.attributes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public class Heal extends AbstractEventAttribute {
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof LivingEntity) {
                final LivingEntity l = (LivingEntity) e;
                final double total = evaluate(recipientValues.get(e).replace("hp", Double.toString(l.getHealth())));
                l.setHealth(Math.min(l.getMaxHealth(), total));
            }
        }
    }
}
