package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.living.ActiveBooster;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class BoosterTriggerEvent extends AbstractCancellable {
    public final Event event;
    public final Player player;
    public final ActiveBooster booster;
    public BoosterTriggerEvent(Event event, Player player, ActiveBooster booster) {
        this.event = event;
        this.player = player;
        this.booster = booster;
    }
}
