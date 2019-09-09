package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.living.ActiveBooster;
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
