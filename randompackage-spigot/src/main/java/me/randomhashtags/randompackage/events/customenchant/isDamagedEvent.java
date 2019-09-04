package me.randomhashtags.randompackage.events.customenchant;

import me.randomhashtags.randompackage.events.DamageEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class isDamagedEvent extends DamageEvent {
    public final Player victim;
    public final LivingEntity damager;
    public final EntityDamageEvent.DamageCause cause;
    public isDamagedEvent(Player victim, LivingEntity damager, double damage) {
        this.victim = victim;
        this.damager = damager;
        setDamage(damage);
        this.cause = null;
    }
    public isDamagedEvent(Player victim, EntityDamageEvent.DamageCause cause) {
        this.victim = victim;
        this.damager = null;
        this.cause = cause;
    }
}
