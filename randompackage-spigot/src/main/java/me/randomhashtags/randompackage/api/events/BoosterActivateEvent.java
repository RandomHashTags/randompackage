package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.addons.Booster;
import org.bukkit.entity.Player;

public class BoosterActivateEvent extends AbstractEvent {
    public final Player player;
    public final Booster booster;
    public final double multiplier;
    public final long duration;
    public BoosterActivateEvent(Player player, Booster booster, double multiplier, long duration) {
        this.player = player;
        this.booster = booster;
        this.multiplier = multiplier;
        this.duration = duration;
    }
}
