package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.living.ActiveBooster;
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
