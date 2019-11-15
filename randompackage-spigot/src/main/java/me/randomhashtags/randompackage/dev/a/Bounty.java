package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.configuration.file.YamlConfiguration;

public class Bounty extends RPFeature {
    private static Bounty instance;
    public static Bounty getBounty() {
        if(instance == null) instance = new Bounty();
        return instance;
    }
    public YamlConfiguration config;

    public String getIdentifier() { return "BOUNTY"; }
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Bounty &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }
}
