package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;

public class AttributeRemovePotionEffect extends AbstractEventAttribute {
    public String getIdentifier() { return "REMOVEPOTIONEFFECT"; }
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            final String v =  recipientValues.get(e);
            if(v != null && e instanceof LivingEntity) {
                ((LivingEntity) e).removePotionEffect(getPotionEffectType(v));
            }
        }
    }
}
