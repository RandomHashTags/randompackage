package me.randomhashtags.randompackage.events;

import org.bukkit.entity.Player;

public class KothCaptureEvent extends RPEventCancellable {
    public KothCaptureEvent(Player player) {
        super(player);
    }
}
