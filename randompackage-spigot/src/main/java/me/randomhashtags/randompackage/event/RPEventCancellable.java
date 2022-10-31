package me.randomhashtags.randompackage.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

public abstract class RPEventCancellable extends RPEvent implements Cancellable {
    private boolean cancelled;
    public RPEventCancellable(@NotNull Player player) {
        super(player);
    }
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
