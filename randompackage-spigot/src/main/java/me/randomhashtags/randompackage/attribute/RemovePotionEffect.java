package me.randomhashtags.randompackage.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class RemovePotionEffect extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues, HashMap<String, String> valueReplacements) {
        for(Entity e : recipientValues.keySet()) {
            removePotionEffect(e, recipientValues.get(e), valueReplacements);
        }
    }
    private void removePotionEffect(Entity entity, String value, HashMap<String, String> valueReplacements) {
        if(entity instanceof LivingEntity) {
            final LivingEntity l = (LivingEntity) entity;
            final String[] values = replaceValue(value, valueReplacements).split(":");
            final PotionEffectType type = getPotionEffectType(values[0]);
            if(type != null) {
                l.removePotionEffect(type);
                if(values.length >= 2 && Boolean.parseBoolean(values[1])) {
                    final HashMap<String, String> replacements = new HashMap<>();
                    if(valueReplacements != null) {
                        replacements.putAll(valueReplacements);
                    }
                    replacements.put("{POTION_EFFECT}", type.getName());
                    sendStringListMessage(entity, getRPConfig("custom enchants", "_settings.yml").getStringList("messages.remove potion effect"), replacements);
                }
            }
        }
    }
}
