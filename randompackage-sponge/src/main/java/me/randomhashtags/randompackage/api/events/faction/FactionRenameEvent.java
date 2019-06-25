package me.randomhashtags.randompackage.api.events.faction;


import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import org.spongepowered.api.entity.living.player.Player;

public class FactionRenameEvent extends RandomPackageEvent {
    public final Player renamer;
    public final String oldFactionName, factionName;
    public FactionRenameEvent(Player renamer, String oldFactionName, String factionName) {
        this.renamer = renamer;
        this.oldFactionName = oldFactionName;
        this.factionName = factionName;
    }
}
