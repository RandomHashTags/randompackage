package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public class AttributeSetHealth extends AbstractEventAttribute {
    public String getIdentifier() { return "SETHEALTH"; }
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final String value = recipientValues.get(e);
            if(value != null && e instanceof LivingEntity) {
                final LivingEntity l = (LivingEntity) e;
                final double hp = l.getHealth(), max = l.getMaxHealth(), total = evaluate(value.replace("hp", Double.toString(hp)));
                l.setHealth(total < 0.00 ? 0.00 : Math.min(total, max));
            }
        }
    }
}
