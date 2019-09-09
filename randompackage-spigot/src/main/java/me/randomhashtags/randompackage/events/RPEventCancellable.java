package me.randomhashtags.randompackage.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class RPEventCancellable extends RPEvent implements Cancellable {
    private boolean cancelled;
    public RPEventCancellable(Player player) {
        super(player);
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }
}
