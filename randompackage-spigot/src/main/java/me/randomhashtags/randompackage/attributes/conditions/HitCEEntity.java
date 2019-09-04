package me.randomhashtags.randompackage.attributes.conditions;

import me.randomhashtags.randompackage.addons.living.LivingCustomEnchantEntity;
import me.randomhashtags.randompackage.attributes.AbstractEventCondition;
import me.randomhashtags.randompackage.events.customenchant.PvAnyEvent;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class HitCEEntity extends AbstractEventCondition {
    @Override
    public boolean check(Event event, Entity entity) {
        if(event instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            return e.getDamager().equals(entity) && LivingCustomEnchantEntity.living.containsKey(e.getEntity().getUniqueId());
        } else if(event instanceof PvAnyEvent) {
            final PvAnyEvent e = (PvAnyEvent) event;
            return e.damager.equals(entity) && LivingCustomEnchantEntity.living.containsKey(e.victim.getUniqueId());
        }
        return false;
    }
}
