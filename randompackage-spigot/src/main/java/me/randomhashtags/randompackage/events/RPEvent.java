package me.randomhashtags.randompackage.events;

import org.bukkit.entity.Player;

public class RPEvent extends AbstractEvent {
    private Player player;
    public RPEvent(Player player) {
        this.player = player;
    }
    public Player getPlayer() { return player; }
}
