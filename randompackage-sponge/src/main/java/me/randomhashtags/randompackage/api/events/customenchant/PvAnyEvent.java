package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.projectile.Projectile;
import org.spongepowered.api.event.Cancellable;

public class PvAnyEvent extends RandomPackageEvent implements Cancellable {
    private boolean cancelled;
    public final Player damager;
    public final Living victim;
    public final Projectile proj;
    public double damage;
    public PvAnyEvent(Player damager, Living victim, double damage) {
        this.damager = damager;
        this.victim = victim;
        this.damage = damage;
        this.proj = null;
    }
    public PvAnyEvent(Player damager, Living victim, double damage, Projectile proj) {
        this.damager = damager;
        this.victim = victim;
        this.damage = damage;
        this.proj = proj;
    }

    public void setCancelled(boolean cancel) { cancelled = cancel; }
    public boolean isCancelled() { return cancelled; }
}
