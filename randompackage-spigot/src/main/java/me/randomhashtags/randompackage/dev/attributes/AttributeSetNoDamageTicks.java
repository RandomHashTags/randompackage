package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public class AttributeSetNoDamageTicks extends AbstractEventAttribute {
    public String getIdentifier() { return "SETNODAMAGETICKS"; }
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        if(recipientValues != null) {
            for(Entity e : recipientValues.keySet()) {
                final String v = recipientValues.get(e);
                if(v != null && e instanceof LivingEntity) {
                    ((LivingEntity) e).setNoDamageTicks(Integer.parseInt(v));
                }
            }
        }
    }
}
