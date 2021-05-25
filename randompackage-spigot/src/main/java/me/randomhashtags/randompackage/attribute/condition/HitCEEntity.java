package me.randomhashtags.randompackage.attribute.condition;

import me.randomhashtags.randompackage.addon.living.LivingCustomEnchantEntity;
import me.randomhashtags.randompackage.attribute.AbstractEventCondition;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public final class HitCEEntity extends AbstractEventCondition {
    @Override
    public boolean check(Event event, Entity entity) {
        if(event instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent damageEvent = (EntityDamageByEntityEvent) event;
            return damageEvent.getDamager().equals(entity) && LivingCustomEnchantEntity.LIVING.containsKey(damageEvent.getEntity().getUniqueId());
        }
        return false;
    }
}
