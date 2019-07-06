package me.randomhashtags.randompackage.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;

public class MysteryMobSpawnerOpenEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public final Player player;
    public String entity;
    public MysteryMobSpawnerOpenEvent(Player player, String entity) {
        this.player = player;
        this.entity = entity;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
}
