package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.active.ActiveBooster;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public class BoosterPreTriggerEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public final Event event;
    public final Player player;
    public final ActiveBooster booster;
    public BoosterPreTriggerEvent(Event event, Player player, ActiveBooster booster) {
        this.event = event;
        this.player = player;
        this.booster = booster;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
