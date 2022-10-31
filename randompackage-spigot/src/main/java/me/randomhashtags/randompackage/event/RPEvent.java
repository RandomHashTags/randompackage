package me.randomhashtags.randompackage.event;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class RPEvent extends AbstractEvent {
    private final Player player;
    public RPEvent(@NotNull Player player) {
        this.player = player;
    }
    @NotNull
    public Player getPlayer() {
        return player;
    }
}
