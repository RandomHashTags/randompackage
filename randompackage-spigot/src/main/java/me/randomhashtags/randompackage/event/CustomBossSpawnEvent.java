package me.randomhashtags.randompackage.event;

import me.randomhashtags.randompackage.addon.living.LivingCustomBoss;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public class CustomBossSpawnEvent extends AbstractCancellable {
    public final LivingEntity summoner;
    public final LivingCustomBoss boss;
    public final Location location;
    public CustomBossSpawnEvent(LivingEntity summoner, Location location, LivingCustomBoss boss) {
        this.summoner = summoner;
        this.location = location;
        this.boss = boss;
    }
}
