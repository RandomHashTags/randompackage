package me.randomhashtags.randompackage.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class MCMMOXpGainEvent extends AbstractEvent implements Cancellable {
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
}
