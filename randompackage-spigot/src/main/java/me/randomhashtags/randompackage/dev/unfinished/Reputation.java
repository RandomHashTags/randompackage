package me.randomhashtags.randompackage.dev.unfinished;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.configuration.file.YamlConfiguration;

public class Reputation extends RPFeature {
    private static Reputation instance;
    public static Reputation getReputation() {
        if(instance == null) instance = new Reputation();
        return instance;
    }

    public YamlConfiguration config;

    public String getIdentifier() { return "REPUTATION"; }
    protected RPFeature getFeature() { return getReputation(); }
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Reputation &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }
}
