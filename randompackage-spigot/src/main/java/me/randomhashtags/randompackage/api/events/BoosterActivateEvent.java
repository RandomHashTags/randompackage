package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.addons.Booster;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;

public class BoosterActivateEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public final OfflinePlayer activator;
    public final Booster booster;
    public double multiplier;
    public long duration;
    public BoosterActivateEvent(OfflinePlayer activator, Booster booster, double multiplier, long duration) {
        this.activator = activator;
        this.booster = booster;
        this.multiplier = multiplier;
        this.duration = duration;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
