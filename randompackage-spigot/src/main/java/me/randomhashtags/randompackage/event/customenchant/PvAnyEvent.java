package me.randomhashtags.randompackage.event.customenchant;

import me.randomhashtags.randompackage.event.DamageEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;

public class PvAnyEvent extends DamageEvent {
    private Player damager;
    private LivingEntity victim;
    private Projectile proj;
    public PvAnyEvent(Player damager, LivingEntity victim, double damage) {
        super(damager, victim, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
        this.damager = damager;
        this.victim = victim;
    }
    public PvAnyEvent(Player damager, LivingEntity victim, double damage, Projectile proj) {
        this(damager, victim, damage);
        this.proj = proj;
    }
    @Override
    public LivingEntity getEntity() { return victim; }
    @Override
    public Player getDamager() { return damager; }
    public Projectile getProjectile() { return proj; }
}
