package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class SetHealth extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity entity : recipientValues.keySet()) {
            if(entity instanceof LivingEntity) {
                final LivingEntity l = (LivingEntity) entity;
                final String value = recipientValues.get(entity);
                if(value != null) {
                    final double total = evaluate(value);
                    l.setHealth(total < 0.00 ? 0.00 : Math.min(total, l.getMaxHealth()));
                }
            }
        }
    }
}
