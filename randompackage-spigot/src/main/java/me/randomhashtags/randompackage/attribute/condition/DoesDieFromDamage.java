package me.randomhashtags.randompackage.attribute.condition;

import me.randomhashtags.randompackage.attribute.AbstractEventCondition;
import me.randomhashtags.randompackage.event.isDamagedEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

public final class DoesDieFromDamage extends AbstractEventCondition {
    @Override
    public boolean check(@NotNull Event event, @NotNull String value) {
        if(event instanceof isDamagedEvent) {
            final isDamagedEvent damageEvent = (isDamagedEvent) event;
            return damageEvent.getEntity().getHealth() - damageEvent.getDamage() <= 0.00 == Boolean.parseBoolean(value);
        } else if(event instanceof EntityDamageEvent) {
            final EntityDamageEvent damageEvent = (EntityDamageEvent) event;
            return damageEvent instanceof LivingEntity && ((LivingEntity) damageEvent.getEntity()).getHealth() - damageEvent.getDamage() <= 0.00 == Boolean.parseBoolean(value);
        }
        return false;
    }
}
