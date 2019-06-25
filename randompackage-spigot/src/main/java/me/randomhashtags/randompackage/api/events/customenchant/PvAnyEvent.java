package me.randomhashtags.randompackage.api.events.customenchant;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PvAnyEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
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
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
