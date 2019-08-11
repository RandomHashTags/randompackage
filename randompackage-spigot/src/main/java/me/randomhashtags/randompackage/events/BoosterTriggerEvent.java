package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.active.ActiveBooster;
import me.randomhashtags.randompackage.addons.objects.ExecutedEventAttributes;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.List;

public class BoosterTriggerEvent extends AbstractEvent {
    public final Event event;
    public final Player player;
    public final ActiveBooster booster;
    public final List<ExecutedEventAttributes> executeAttributes;
    public BoosterTriggerEvent(Event event, Player player, ActiveBooster booster, List<ExecutedEventAttributes> executedAttributes) {
        this.event = event;
        this.player = player;
        this.booster = booster;
        this.executeAttributes = executedAttributes;
    }
}
