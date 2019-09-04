package me.randomhashtags.randompackage.events.customenchant;

import me.randomhashtags.randompackage.events.DamageEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

public class PvAnyEvent extends DamageEvent {
    public final Player damager;
    public final LivingEntity victim;
    public final Projectile proj;
    public PvAnyEvent(Player damager, LivingEntity victim, double damage) {
        this.damager = damager;
        this.victim = victim;
        setDamage(damage);
        this.proj = null;
    }
    public PvAnyEvent(Player damager, LivingEntity victim, double damage, Projectile proj) {
        this.damager = damager;
        this.victim = victim;
        setDamage(damage);
        this.proj = proj;
    }
}
