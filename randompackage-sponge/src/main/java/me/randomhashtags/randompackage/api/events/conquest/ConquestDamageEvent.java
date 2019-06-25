package me.randomhashtags.randompackage.api.events.conquest;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.conquests.LivingConquestChest;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;

public class ConquestDamageEvent extends RandomPackageEvent implements Cancellable {
    private boolean cancelled;
    public final Player damager;
    public final LivingConquestChest livingConquestChest;
    public double damage;
    public ConquestDamageEvent(Player damager, LivingConquestChest livingConquestChest, double damage) {
        this.damager = damager;
        this.livingConquestChest = livingConquestChest;
        this.damage = damage;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
}
