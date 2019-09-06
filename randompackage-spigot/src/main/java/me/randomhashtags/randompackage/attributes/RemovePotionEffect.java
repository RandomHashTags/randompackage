package me.randomhashtags.randompackage.attributes;

import me.randomhashtags.randompackage.events.customenchant.CustomEnchantProcEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class RemovePotionEffect extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            removePotionEffect(event, e, recipientValues.get(e));
        }
    }
    private void removePotionEffect(Event event, Entity entity, String value) {
        if(entity instanceof LivingEntity) {
            final LivingEntity l = (LivingEntity) entity;
            final String[] values = value.split(":");
            final PotionEffectType type = getPotionEffectType(values[0]);
            if(type != null) {
                l.removePotionEffect(type);
                if(values.length >= 2 && Boolean.parseBoolean(values[1])) {
                    final HashMap<String, String> replacements = new HashMap<>();
                    if(event instanceof CustomEnchantProcEvent) {
                        final CustomEnchantProcEvent e = (CustomEnchantProcEvent) event;
                        replacements.put("{ENCHANT}", e.enchant.getName() + " " + api.toRoman(e.level));
                    }
                    replacements.put("{POTION_EFFECT}", type.getName());
                    sendStringListMessage(entity, getRPConfig(null, "custom enchants.yml").getStringList("messages.remove potion effect"), replacements);
                }
            }
        }
    }
}
