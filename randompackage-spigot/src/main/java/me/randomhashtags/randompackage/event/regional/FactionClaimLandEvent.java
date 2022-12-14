package me.randomhashtags.randompackage.event.regional;

import me.randomhashtags.randompackage.event.AbstractEvent;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public final class FactionClaimLandEvent extends AbstractEvent {
    public final Player player;
    public final String faction;
    public final Chunk chunk;
    public FactionClaimLandEvent(Player player, String faction, Chunk chunk) {
        this.player = player;
        this.faction = faction;
        this.chunk = chunk;
    }
}
