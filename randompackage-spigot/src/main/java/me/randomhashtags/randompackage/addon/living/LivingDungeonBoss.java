package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.dev.DungeonBoss;
import org.bukkit.Location;

public class LivingDungeonBoss {
    private DungeonBoss type;
    private Location l;
    public LivingDungeonBoss(DungeonBoss type, Location l) {
        this.type = type;
        this.l = l;
    }
    public DungeonBoss getType() { return type; }
    public Location getSpawnLocation() { return l; }
}
