package me.randomhashtags.randompackage.api.events.conquest;

import me.randomhashtags.randompackage.utils.classes.conquests.LivingConquestChest;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ConquestDamageEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
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
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
