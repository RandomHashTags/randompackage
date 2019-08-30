package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public class AttributeDamage extends AbstractEventAttribute {
    public String getIdentifier() { return "DAMAGE"; }
    public void execute(Object value) {}
    public void execute(HashMap<Entity, Object> recipientValues) {
        if(recipientValues != null) {
            for(Entity e : recipientValues.keySet()) {
                final LivingEntity l = e instanceof LivingEntity ? (LivingEntity) e : null;
                if(l != null) {
                    final Object v = recipientValues.get(e);
                    if(v != null) {
                        l.damage((double) v);
                    }
                }
            }
        }
    }
}
