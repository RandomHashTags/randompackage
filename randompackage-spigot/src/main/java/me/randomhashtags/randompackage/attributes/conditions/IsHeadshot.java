package me.randomhashtags.randompackage.attributes.conditions;

import me.randomhashtags.randompackage.attributes.AbstractEventCondition;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileHitEvent;

public class IsHeadshot extends AbstractEventCondition {
    @Override
    public boolean check(Event event, String value) {
        if(event instanceof ProjectileHitEvent) {
            final ProjectileHitEvent e = (ProjectileHitEvent) event;
            final LivingEntity victim = getHitEntity(e);
            if(victim != null) {
                final Projectile p = e.getEntity();
                return p.getLocation().getY() > victim.getEyeLocation().getY();
            }
        }
        return false;
    }
}
