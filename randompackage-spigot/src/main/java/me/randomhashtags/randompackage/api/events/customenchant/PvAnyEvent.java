package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.api.events.AbstractEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;

public class PvAnyEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public final Player damager;
    public final LivingEntity victim;
    public final Projectile proj;
    public double damage;
    public PvAnyEvent(Player damager, LivingEntity victim, double damage) {
        this.damager = damager;
        this.victim = victim;
        this.damage = damage;
        this.proj = null;
    }
    public PvAnyEvent(Player damager, LivingEntity victim, double damage, Projectile proj) {
        this.damager = damager;
        this.victim = victim;
        this.damage = damage;
        this.proj = proj;
    }

    public void setCancelled(boolean cancel) { cancelled = cancel; }
    public boolean isCancelled() { return cancelled; }
}
