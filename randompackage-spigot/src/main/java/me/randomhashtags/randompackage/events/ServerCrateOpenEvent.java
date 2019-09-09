package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.ServerCrate;
import org.bukkit.entity.Player;

public class ServerCrateOpenEvent extends RPEventCancellable {
    public final ServerCrate crate;
    public ServerCrateOpenEvent(Player player, ServerCrate crate) {
        super(player);
        this.crate = crate;
    }
}
