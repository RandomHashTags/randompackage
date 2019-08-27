package me.randomhashtags.randompackage.events.regional;

import me.randomhashtags.randompackage.events.AbstractEvent;
import org.bukkit.entity.Player;

public class RegionDisbandEvent extends AbstractEvent {
    public final Player disbander;
    public final String identifier;
    public RegionDisbandEvent(Player disbander, String identifier) {
        this.disbander = disbander;
        this.identifier = identifier;
    }
}
