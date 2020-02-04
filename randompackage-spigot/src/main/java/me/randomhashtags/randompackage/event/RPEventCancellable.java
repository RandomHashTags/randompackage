package me.randomhashtags.randompackage.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public abstract class RPEventCancellable extends RPEvent implements Cancellable {
    private boolean cancelled;
    public RPEventCancellable(Player player) {
        super(player);
    }
    public boolean isCancelled() {
        return cancelled;
    }
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
