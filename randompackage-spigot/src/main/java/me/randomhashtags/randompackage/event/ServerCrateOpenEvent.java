package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.ServerCrate;
import org.bukkit.entity.Player;

public final class ServerCrateOpenEvent extends RPEventCancellable {
    public final ServerCrate crate;
    public ServerCrateOpenEvent(Player player, ServerCrate crate) {
        super(player);
        this.crate = crate;
    }
}
