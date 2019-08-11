package me.randomhashtags.randompackage.api.unfinished;

import me.randomhashtags.randompackage.utils.RPFeature;

public class FactionPoints extends RPFeature {
    private static FactionPoints instance;
    public static FactionPoints getFactionPoints() {
        if(instance == null) instance = new FactionPoints();
        return instance;
    }

    public String getIdentifier() { return "FACTION_POINTS"; }
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Faction Points &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
        instance = null;
    }
}
