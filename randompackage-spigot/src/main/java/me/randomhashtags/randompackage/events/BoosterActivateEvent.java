package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.Booster;
import org.bukkit.OfflinePlayer;

public class BoosterActivateEvent extends AbstractCancellable {
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
}
