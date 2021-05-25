package me.randomhashtags.randompackage.event.regional;

import me.randomhashtags.randompackage.event.AbstractEvent;
import org.bukkit.entity.Player;

public final class RegionRenameEvent extends AbstractEvent {
    public final Player renamer;
    public final String oldFactionName, factionName;
    public RegionRenameEvent(Player renamer, String oldFactionName, String factionName) {
        this.renamer = renamer;
        this.oldFactionName = oldFactionName;
        this.factionName = factionName;
    }
}
