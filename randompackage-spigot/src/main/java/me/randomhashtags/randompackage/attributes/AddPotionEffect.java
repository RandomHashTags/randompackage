package me.randomhashtags.randompackage.attributes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class AddPotionEffect extends AbstractEventAttribute {
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            addPotionEffect(e, recipientValues.get(e));
        }
    }
    private void addPotionEffect(Entity entity, String value) {
        if(entity instanceof LivingEntity) {
            final LivingEntity l = (LivingEntity) entity;
            final String[] values = value.split(":");
            final PotionEffectType type = getPotionEffectType(values[0]);
            if(type != null) {
                final int amplifier = (int) evaluate(values[1]), duration = (int) evaluate(values[2]);
                l.addPotionEffect(new PotionEffect(type, duration, amplifier));
                if(values.length >= 4 && Boolean.parseBoolean(values[3])) {
                    final HashMap<String, String> replacements = new HashMap<>();
                    replacements.put("{DURATION_TICKS}", Integer.toString(duration));
                    replacements.put("{DURATION_SECONDS}", Integer.toString(duration/20));
                    replacements.put("{AMPLIFIER}", Integer.toString(amplifier));
                    replacements.put("{LEVEL}", Integer.toString(amplifier-1));
                    sendStringListMessage(entity, getRPConfig(null, "custom enchants.yml").getStringList("messages.apply potion effect"), replacements);
                }
            }
        }
    }
}
