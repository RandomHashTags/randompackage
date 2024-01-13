package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class RemovePotionEffect extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull PendingEventAttribute pending, @NotNull HashMap<String, String> valueReplacements) {
        final HashMap<String, Entity> entities = pending.getEntities();
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            removePotionEffect(entities, e, recipientValues.get(e), valueReplacements);
        }
    }
    private void removePotionEffect(HashMap<String, Entity> entities, Entity entity, String value, HashMap<String, String> valueReplacements) {
        if(entity instanceof LivingEntity) {
            final LivingEntity l = (LivingEntity) entity;
            final String[] values = replaceValue(entities, value, valueReplacements).split(":");
            final String effects = values[0];
            final boolean sendMessage = values.length >= 2 && Boolean.parseBoolean(values[1]);
            if(effects.contains(",")) {
                for(String string : effects.split(",")) {
                    removePotionEffect(l, string, valueReplacements, sendMessage);
                }
            } else {
                removePotionEffect(l, effects, valueReplacements, sendMessage);
            }
        }
    }
    private void removePotionEffect(LivingEntity entity, String input, HashMap<String, String> valueReplacements, boolean sendMessage) {
        final PotionEffectType type = get_potion_effect_type(input);
        if(type != null) {
            entity.removePotionEffect(type);
            if(sendMessage) {
                sendMessage(entity, type, valueReplacements);
            }
        }
    }
    private void sendMessage(LivingEntity entity, PotionEffectType type, HashMap<String, String> valueReplacements) {
        final HashMap<String, String> replacements = new HashMap<>();
        if(valueReplacements != null) {
            replacements.putAll(valueReplacements);
        }
        replacements.put("{POTION_EFFECT}", type.getName());
        sendStringListMessage(entity, getRPConfig("custom enchants", "_settings.yml").getStringList("messages.remove potion effect"), replacements);
    }
}
