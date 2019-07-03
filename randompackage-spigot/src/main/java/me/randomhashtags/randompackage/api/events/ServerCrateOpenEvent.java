package me.randomhashtags.randompackage.api.events;

import me.randomhashtags.randompackage.recode.api.events.AbstractEvent;
import me.randomhashtags.randompackage.recode.api.addons.usingFile.FileServerCrate;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class ServerCrateOpenEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public final Player player;
    public final FileServerCrate crate;
    public ServerCrateOpenEvent(Player player, FileServerCrate crate) {
        this.player = player;
        this.crate = crate;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
}
