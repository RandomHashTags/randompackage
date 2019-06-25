package me.randomhashtags.randompackage.api.events.faction;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FactionRenameEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    public final Player renamer;
    public final String oldFactionName, factionName;
    public FactionRenameEvent(Player renamer, String oldFactionName, String factionName) {
        this.renamer = renamer;
        this.oldFactionName = oldFactionName;
        this.factionName = factionName;
    }

    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
