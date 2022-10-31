package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.ServerCrate;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public final class ServerCrateOpenEvent extends RPEventCancellable {
    @NotNull public final ServerCrate crate;
    public ServerCrateOpenEvent(@NotNull Player player, @NotNull ServerCrate crate) {
        super(player);
        this.crate = crate;
    }
}
