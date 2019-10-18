package me.randomhashtags.randompackage.event;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

public class isDamagedEvent extends DamageEvent {
    private Player victim;
    private LivingEntity damager;
    public isDamagedEvent(Player victim, LivingEntity damager, double damage) {
        super(damager, victim, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
        this.victim = victim;
        this.damager = damager;
    }
    public isDamagedEvent(Player victim, EntityDamageEvent.DamageCause cause, double damage) {
        super(null, victim, cause, damage);
        this.victim = victim;
        this.damager = null;
    }
    @Override
    public Player getEntity() { return victim; }
    @Override
    public LivingEntity getDamager() { return damager; }
}
