package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.ServerCrate;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class ServerCrateOpenEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public final Player player;
    public final ServerCrate crate;
    public ServerCrateOpenEvent(Player player, ServerCrate crate) {
        this.player = player;
        this.crate = crate;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
}
