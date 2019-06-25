package me.randomhashtags.randompackage.api.events.faction;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FactionDisbandEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    public final Player disbander;
    public final String factionName;
    public FactionDisbandEvent(Player disbander, String factionName) {
        this.disbander = disbander;
        this.factionName = factionName;
    }

    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
