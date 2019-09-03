package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.ServerCrate;
import org.bukkit.entity.Player;

public class ServerCrateOpenEvent extends AbstractCancellable {
    public final Player player;
    public final ServerCrate crate;
    public ServerCrateOpenEvent(Player player, ServerCrate crate) {
        this.player = player;
        this.crate = crate;
    }
}
