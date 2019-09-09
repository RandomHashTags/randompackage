package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.Booster;
import org.bukkit.entity.Player;

public class BoosterPreActivateEvent extends AbstractCancellable {
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
}
