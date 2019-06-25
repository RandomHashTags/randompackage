package me.randomhashtags.randompackage.api.events.servercrates;

import me.randomhashtags.randompackage.utils.classes.servercrate.ServerCrate;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ServerCrateOpenEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    public final Player player;
    public final ServerCrate crate;
    public ServerCrateOpenEvent(Player player, ServerCrate crate) {
        this.player = player;
        this.crate = crate;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
    public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }
}
