package me.randomhashtags.randompackage.dev.attributes;

import me.randomhashtags.randompackage.dev.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;

public class AttributeAddPotionEffect extends AbstractEventAttribute {
    public String getIdentifier() { return "ADDPOTIONEFFECT"; }
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        if(recipientValues != null) {
            for(Entity e : recipientValues.keySet()) {
                if(e instanceof LivingEntity) {
                    final LivingEntity l = (LivingEntity) e;
                    final String v = recipientValues.get(e);
                    if(v != null) {
                        final String[] values = v.split(":");
                        l.addPotionEffect(new PotionEffect(getPotionEffectType(values[0]), (int) evaluate(values[2]), (int) evaluate(values[1])));
                    }
                }
            }
        }
    }
}
