package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.dev.dungeons.DungeonBoss;
import org.bukkit.Location;

public final class LivingDungeonBoss {
    private final DungeonBoss type;
    private final Location l;
    public LivingDungeonBoss(DungeonBoss type, Location l) {
        this.type = type;
        this.l = l;
    }
    public DungeonBoss getType() {
        return type;
    }
    public Location getSpawnLocation() {
        return l;
    }
}
