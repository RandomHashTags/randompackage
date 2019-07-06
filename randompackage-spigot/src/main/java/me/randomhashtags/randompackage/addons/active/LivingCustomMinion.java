package me.randomhashtags.randompackage.addons.active;

import me.randomhashtags.randompackage.api.events.CustomMinionDeathEvent;
import me.randomhashtags.randompackage.addons.objects.CustomMinion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.UUID;

public class LivingCustomMinion {
    public static HashMap<UUID, LivingCustomMinion> living;
    public LivingEntity entity, target;
    public CustomMinion type;
    public LivingCustomBoss parent;
    public LivingCustomMinion(LivingEntity entity, LivingEntity target, CustomMinion type, LivingCustomBoss parent) {
        if(living == null) {
            living = new HashMap<>();
        }
        entity.setCustomName(ChatColor.translateAlternateColorCodes('&', type.name));
        entity.setCustomNameVisible(true);
        if(entity instanceof Creature && target != null) {
            ((Creature) entity).setTarget(target);
            this.target = target;
        }
        this.entity = entity;
        this.type = type;
        this.parent = parent;
        living.put(entity.getUniqueId(), this);
    }
    public void kill(Event damagecause) {
        final CustomMinionDeathEvent e = new CustomMinionDeathEvent(this, damagecause);
        Bukkit.getPluginManager().callEvent(e);
        delete();
    }
    public void delete() {
        living.remove(entity.getUniqueId());
        parent.minions.remove(this);
        entity.setHealth(0.00);
        entity = null;
        type = null;
        parent = null;
        target = null;
        if(living.isEmpty()) {
            living = null;
        }
    }
    public LivingEntity getTarget() { return target; }
    public static void deleteAll() {
        living = null;
    }
}