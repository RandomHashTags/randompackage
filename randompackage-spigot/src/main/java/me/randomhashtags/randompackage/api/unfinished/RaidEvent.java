package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.utils.RPFeature;

public class RaidEvent extends RPFeature {
    private static RaidEvent instance;
    public static RaidEvent getRaidEvent() {
        if(instance == null) instance = new RaidEvent();
        return instance;
    }

    public String getIdentifier() { return "RAID_EVENT"; }

    public void load() {
    }
    public void unload() {
        instance = null;
    }
}
