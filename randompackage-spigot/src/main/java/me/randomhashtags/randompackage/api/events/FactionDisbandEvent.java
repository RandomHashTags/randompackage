package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.recode.api.events.AbstractEvent;
import org.bukkit.entity.Player;

public class FactionDisbandEvent extends AbstractEvent {
    public final Player disbander;
    public final String factionName;
    public FactionDisbandEvent(Player disbander, String factionName) {
        this.disbander = disbander;
        this.factionName = factionName;
    }
}
