package me.randomhashtags.randompackage.event.booster;

import me.randomhashtags.randompackage.addon.living.ActiveBooster;
import me.randomhashtags.randompackage.event.RPEventCancellable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class BoosterTriggerEvent extends RPEventCancellable {
    public final Event event;
    public final ActiveBooster booster;
    public BoosterTriggerEvent(Event event, Player player, ActiveBooster booster) {
        super(player);
        this.event = event;
        this.booster = booster;
    }
}
