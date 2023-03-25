package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.event.mob.CustomMinionDeathEvent;
import me.randomhashtags.randompackage.addon.obj.CustomMinion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.UUID;

public final class LivingCustomMinion {
    public static HashMap<UUID, LivingCustomMinion> LIVING;
    public LivingEntity entity, target;
    public CustomMinion type;
    public LivingCustomBoss parent;
    public LivingCustomMinion(LivingEntity entity, LivingEntity target, CustomMinion type, LivingCustomBoss parent) {
        if(LIVING == null) {
            LIVING = new HashMap<>();
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
        LIVING.put(entity.getUniqueId(), this);
    }
    public void kill(Event damage_cause) {
        final CustomMinionDeathEvent e = new CustomMinionDeathEvent(this, damage_cause);
        Bukkit.getPluginManager().callEvent(e);
        delete();
    }
    public void delete() {
        LIVING.remove(entity.getUniqueId());
        parent.minions.remove(this);
        entity.setHealth(0.00);
        entity = null;
        type = null;
        parent = null;
        target = null;
        if(LIVING.isEmpty()) {
            LIVING = null;
        }
    }
    public static void deleteAll() {
        LIVING = null;
    }
}