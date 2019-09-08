package me.randomhashtags.randompackage.dev.unfinished;

import me.randomhashtags.randompackage.utils.RPFeature;
import org.bukkit.configuration.file.YamlConfiguration;

public class Bounty extends RPFeature {
    private static Bounty instance;
    public static Bounty getBounty() {
        if(instance == null) instance = new Bounty();
        return instance;
    }
    public YamlConfiguration config;

    public String getIdentifier() { return "BOUNTY"; }
    protected RPFeature getFeature() { return getBounty(); }
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Bounty &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }
}
