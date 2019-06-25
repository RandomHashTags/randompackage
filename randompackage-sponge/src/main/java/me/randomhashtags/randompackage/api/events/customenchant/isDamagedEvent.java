package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.entity.damage.source.DamageSource;

public class isDamagedEvent extends RandomPackageEvent implements Cancellable {
    private boolean cancelled;
    public final Player victim;
    public final Living damager;
    public double damage;
    public final DamageSource cause;
    public isDamagedEvent(Player victim, Living damager, double damage) {
        this.victim = victim;
        this.damager = damager;
        this.damage = damage;
        this.cause = null;
    }
    public isDamagedEvent(Player victim, DamageSource cause) {
        this.victim = victim;
        this.damager = null;
        this.cause = cause;
    }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
    public boolean isCancelled() { return cancelled; }
}
