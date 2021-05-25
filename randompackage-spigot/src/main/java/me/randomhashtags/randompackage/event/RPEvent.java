package me.randomhashtags.randompackage.event;

import org.bukkit.entity.Player;

public abstract class RPEvent extends AbstractEvent {
    private final Player player;
    public RPEvent(Player player) {
        this.player = player;
    }
    public Player getPlayer() {
        return player;
    }
}
