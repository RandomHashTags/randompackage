package me.randomhashtags.randompackage.events.regional;

import me.randomhashtags.randompackage.events.AbstractEvent;
import org.bukkit.entity.Player;

public class FactionLeaveEvent extends AbstractEvent {
    public final Player player;
    public final String faction;
    public FactionLeaveEvent(Player player, String faction) {
        this.player = player;
        this.faction = faction;
    }
}
