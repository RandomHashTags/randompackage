package me.randomhashtags.randompackage.api.events.faction;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import org.spongepowered.api.entity.living.player.Player;

public class FactionDisbandEvent extends RandomPackageEvent {
    public final Player disbander;
    public final String factionName;
    public FactionDisbandEvent(Player disbander, String factionName) {
        this.disbander = disbander;
        this.factionName = factionName;
    }
}
