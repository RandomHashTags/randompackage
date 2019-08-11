package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.Booster;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class BoosterPreActivateEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public final Player player;
    public final Booster booster;
    public double multiplier;
    public long duration;
    public BoosterPreActivateEvent(Player player, Booster booster, double multiplier, long duration) {
        this.player = player;
        this.booster = booster;
        this.multiplier = multiplier;
        this.duration = duration;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
