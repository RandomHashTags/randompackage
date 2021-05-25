package me.randomhashtags.randompackage.event.regional;

import me.randomhashtags.randompackage.event.AbstractEvent;
import org.bukkit.entity.Player;

public final class RegionDisbandEvent extends AbstractEvent {
    public final Player disbander;
    public final String identifier;
    public RegionDisbandEvent(Player disbander, String identifier) {
        this.disbander = disbander;
        this.identifier = identifier;
    }
}
