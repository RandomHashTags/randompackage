package me.randomhashtags.randompackage.attributes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class RemovePotionEffect extends AbstractEventAttribute {
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            removePotionEffect(e, recipientValues.get(e));
        }
    }
    private void removePotionEffect(Entity entity, String value) {
        if(entity instanceof LivingEntity) {
            final LivingEntity l = (LivingEntity) entity;
            final String[] values = value.split(":");
            final PotionEffectType type = getPotionEffectType(values[0]);
            if(type != null) {
                l.removePotionEffect(type);
                if(values.length >= 2 && Boolean.parseBoolean(values[1])) {
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{TYPE}", type.getName());
                    sendStringListMessage(entity, getRPConfig(null, "custom enchants.yml").getStringList("messages.remove potion effect"), replacements);
                }
            }
        }
    }
}
