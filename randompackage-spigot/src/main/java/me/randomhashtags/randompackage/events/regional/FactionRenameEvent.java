package me.randomhashtags.randompackage.events.regional;

import me.randomhashtags.randompackage.events.AbstractEvent;
import org.bukkit.entity.Player;

public class FactionRenameEvent extends AbstractEvent {
    public final Player renamer;
    public final String oldFactionName, factionName;
    public FactionRenameEvent(Player renamer, String oldFactionName, String factionName) {
        this.renamer = renamer;
        this.oldFactionName = oldFactionName;
        this.factionName = factionName;
    }
}
