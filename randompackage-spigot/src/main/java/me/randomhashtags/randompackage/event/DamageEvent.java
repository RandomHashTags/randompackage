package me.randomhashtags.randompackage.event;

import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class DamageEvent extends AbstractCancellable {
    private final Entity damagee;
    private final Entity damager;
    private final EntityDamageEvent.DamageCause cause;
    private double damage;
    public DamageEvent(@NotNull Entity damagee, @NotNull EntityDamageEvent.DamageCause cause, double damage) {
        this(null, damagee, cause, damage);
    }
    public DamageEvent(@Nullable Entity damager, @NotNull Entity damagee, @NotNull EntityDamageEvent.DamageCause cause, double damage) {
        this.damager = damager;
        this.damagee = damagee;
        this.cause = cause;
        this.damage = damage;
    }
    @NotNull
    public Entity getEntity() {
        return damagee;
    }
    @Nullable
    public Entity getDamager() {
        return damager;
    }
    @NotNull
    public EntityDamageEvent.DamageCause getCause() {
        return cause;
    }
    public double getDamage() {
        return damage;
    }
    public void setDamage(double damage) {
        this.damage = damage;
    }
}
