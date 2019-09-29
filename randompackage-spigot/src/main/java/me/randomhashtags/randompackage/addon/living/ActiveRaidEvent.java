package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.dev.RaidEventPhase;
import org.bukkit.World;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class ActiveRaidEvent {
    private long startTime;
    private World world;
    private RaidEventPhase phase;

    public long getStartTime() { return startTime; }
    public String getRuntime() { return api.getRemainingTime(System.currentTimeMillis()-startTime); }

    public int getPlayers() { return world.getPlayers().size(); }
    public RaidEventPhase getPhase() { return phase; }
}
