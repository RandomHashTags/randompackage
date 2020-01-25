package me.randomhashtags.randompackage.addon.living;

import me.randomhashtags.randompackage.addon.RaidEventPhase;
import org.bukkit.World;

import static me.randomhashtags.randompackage.RandomPackageAPI.API;

public class ActiveRaidEvent {
    private long startTime;
    private World world;
    private RaidEventPhase phase;

    public long getStartTime() { return startTime; }
    public String getRuntime() { return API.getRemainingTime(System.currentTimeMillis()-startTime); }

    public int getPlayers() { return world.getPlayers().size(); }
    public RaidEventPhase getPhase() { return phase; }
}
