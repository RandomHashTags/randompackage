package me.randomhashtags.randompackage.event;

import com.sun.istack.internal.NotNull;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageEvent extends AbstractCancellable {
    private Entity damagee, damager;
    private EntityDamageEvent.DamageCause cause;
    private double damage;
    public DamageEvent(@NotNull Entity damagee, @NotNull EntityDamageEvent.DamageCause cause, double damage) {
        this(null, damagee, cause, damage);
    }
    public DamageEvent(@NotNull Entity damager, @NotNull Entity damagee, @NotNull EntityDamageEvent.DamageCause cause, double damage) {
        this.damager = damager;
        this.damagee = damagee;
        this.cause = cause;
        this.damage = damage;
    }
    public Entity getEntity() { return damagee; }
    public Entity getDamager() { return damager; }
    public EntityDamageEvent.DamageCause getCause() { return cause; }
    public double getDamage() { return damage; }
    public void setDamage(double damage) { this.damage = damage; }
}
