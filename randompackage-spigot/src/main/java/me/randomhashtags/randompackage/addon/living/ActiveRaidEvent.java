package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.addon.dev.RaidEventPhase;
import org.bukkit.World;

public final class ActiveRaidEvent {
    private long startTime;
    private World world;
    private RaidEventPhase phase;

    public long getStartTime() {
        return startTime;
    }
    public String getRuntime() {
        return RandomPackageAPI.INSTANCE.getRemainingTime(System.currentTimeMillis()-startTime);
    }

    public int getPlayers() {
        return world.getPlayers().size();
    }
    public RaidEventPhase getPhase() {
        return phase;
    }
}
