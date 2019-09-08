package me.randomhashtags.randompackage.dev.unfinished;

import me.randomhashtags.randompackage.utils.RPFeature;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

public class RaidEvent extends RPFeature {
    private static RaidEvent instance;
    public static RaidEvent getRaidEvent() {
        if(instance == null) instance = new RaidEvent();
        return instance;
    }

    public String getIdentifier() { return "RAID_EVENT"; }
    protected RPFeature getFeature() { return getRaidEvent(); }
    public void load() {
    }
    public void unload() {
    }


    public void didClaimLand(Player player, String faction, Chunk c) {
    }
}
