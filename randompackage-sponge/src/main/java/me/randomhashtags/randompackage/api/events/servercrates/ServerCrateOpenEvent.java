package me.randomhashtags.randompackage.api.events.servercrates;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.servercrate.ServerCrate;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;

public class ServerCrateOpenEvent extends RandomPackageEvent implements Cancellable {
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
