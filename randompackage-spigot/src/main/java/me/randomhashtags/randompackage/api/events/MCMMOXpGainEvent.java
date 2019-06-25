package me.randomhashtags.randompackage.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MCMMOXpGainEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    public final Player player;
    public final Object skill;
    public float xp;
    public MCMMOXpGainEvent(Player player, Object skill, float xp) {
        this.player = player;
        this.skill = skill;
        this.xp = xp;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
