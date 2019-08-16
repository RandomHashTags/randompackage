package me.randomhashtags.randompackage.events;

import me.randomhashtags.randompackage.addons.living.LivingCustomBoss;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;

public class CustomBossSpawnEvent extends AbstractEvent implements Cancellable {
    private boolean cancelled;
    public final LivingEntity summoner;
    public final LivingCustomBoss boss;
    public final Location location;
    public CustomBossSpawnEvent(LivingEntity summoner, Location location, LivingCustomBoss boss) {
        this.summoner = summoner;
        this.location = location;
        this.boss = boss;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
}
