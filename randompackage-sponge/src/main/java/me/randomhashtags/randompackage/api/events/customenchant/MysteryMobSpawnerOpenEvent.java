package me.randomhashtags.randompackage.api.events.customenchant;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;

public class MysteryMobSpawnerOpenEvent extends RandomPackageEvent implements Cancellable {
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
