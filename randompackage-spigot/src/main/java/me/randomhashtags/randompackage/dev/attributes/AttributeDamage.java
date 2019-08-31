package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public class AttributeDamage extends AbstractEventAttribute {
    public String getIdentifier() { return "DAMAGE"; }
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final LivingEntity l = e instanceof LivingEntity ? (LivingEntity) e : null;
            if(l != null) {
                final String v = recipientValues.get(e);
                if(v != null) {
                    l.damage(Double.parseDouble(v));
                }
            }
        }
    }
}
