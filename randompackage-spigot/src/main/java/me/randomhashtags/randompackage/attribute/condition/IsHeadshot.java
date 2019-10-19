package me.randomhashtags.randompackage.attribute.condition;

import me.randomhashtags.randompackage.attribute.AbstractEventCondition;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileHitEvent;

public class IsHeadshot extends AbstractEventCondition {
    @Override
    public boolean check(Event event, String value) {
        if(event instanceof ProjectileHitEvent) {
            final ProjectileHitEvent e = (ProjectileHitEvent) event;
            final Entity victim = getHitEntity(e);
            if(victim instanceof LivingEntity) {
                final Projectile p = e.getEntity();
                return p.getLocation().getY() > ((LivingEntity) victim).getEyeLocation().getY();
            }
        }
        return false;
    }
}
