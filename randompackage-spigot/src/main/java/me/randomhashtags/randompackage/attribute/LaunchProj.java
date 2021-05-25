package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;

public final class LaunchProj extends AbstractEventAttribute {
    @Override
    public void execute(PendingEventAttribute pending, HashMap<String, String> valueReplacements) {
        final Event event = pending.getEvent();
        final HashMap<String, Entity> entities = pending.getEntities();
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        Vector v = null;
        if(event instanceof ProjectileLaunchEvent) {
            v = ((ProjectileLaunchEvent) event).getEntity().getVelocity();
        }
        for(Entity e : recipientValues.keySet()) {
            String value = recipientValues.get(e);
            if(value != null && e instanceof LivingEntity) {
                final LivingEntity l = (LivingEntity) e;
                value = replaceValue(entities, value, valueReplacements);
                final EntityType type = EntityType.valueOf(value.toUpperCase());
                final Projectile proj = (Projectile) l.getWorld().spawnEntity(l.getEyeLocation(), type);
                if(v != null) proj.setVelocity(v);
                proj.setShooter(l);
            }
        }
    }
}
