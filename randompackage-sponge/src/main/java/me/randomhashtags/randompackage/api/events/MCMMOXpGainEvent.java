package me.randomhashtags.randompackage.api.events;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;

public class MCMMOXpGainEvent extends RandomPackageEvent implements Cancellable {
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
