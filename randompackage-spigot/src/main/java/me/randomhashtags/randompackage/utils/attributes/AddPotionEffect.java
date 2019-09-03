package me.randomhashtags.randompackage.utils.attributes;

import me.randomhashtags.randompackage.utils.addons.AbstractEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;

public class AddPotionEffect extends AbstractEventAttribute {
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            if(e instanceof LivingEntity) {
                final String value = recipientValues.get(e);
                final LivingEntity l = (LivingEntity) e;
                final String[] values = value.split(":");
                l.addPotionEffect(new PotionEffect(getPotionEffectType(values[0]), (int) evaluate(values[2]), (int) evaluate(values[1])));
            }
        }
    }
}
