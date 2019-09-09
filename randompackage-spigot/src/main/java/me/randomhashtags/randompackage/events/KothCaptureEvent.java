package me.randomhashtags.randompackage.events;

import org.bukkit.entity.Player;

public class KothCaptureEvent extends AbstractCancellable {
    private Player player;
    public KothCaptureEvent(Player player) {
        this.player = player;
    }
    public Player getPlayer() { return player; }
}
