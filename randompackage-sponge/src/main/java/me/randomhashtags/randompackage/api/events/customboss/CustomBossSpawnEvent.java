package me.randomhashtags.randompackage.api.events.customboss;

import me.randomhashtags.randompackage.api.events.RandomPackageEvent;
import me.randomhashtags.randompackage.utils.classes.custombosses.LivingCustomBoss;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.world.Location;

public class CustomBossSpawnEvent extends RandomPackageEvent implements Cancellable {
    private boolean cancelled;
    public final Living summoner;
    public final LivingCustomBoss boss;
    public final Location location;
    public CustomBossSpawnEvent(Living summoner, Location location, LivingCustomBoss boss) {
        this.summoner = summoner;
        this.location = location;
        this.boss = boss;
    }
    public boolean isCancelled() { return cancelled; }
    public void setCancelled(boolean cancel) { cancelled = cancel; }
}
