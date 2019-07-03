package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.active.LivingConquestChest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class ConquestDamageEvent extends AbstractEvent implements Cancellable {
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
