package me.randomhashtags.randompackage.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class LaunchProj extends AbstractEventAttribute {
    @Override
    public void execute(Event event, HashMap<String, Entity> entities, HashMap<Entity, String> recipientValues, HashMap<String, String> valueReplacements) {
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
