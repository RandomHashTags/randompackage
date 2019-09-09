package me.randomhashtags.randompackage.dev.unfinished;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.configuration.file.YamlConfiguration;

public class FactionTop extends RPFeature {
    private static FactionTop instance;
    public static FactionTop getFactionTop() {
        if(instance == null) instance = new FactionTop();
        return instance;
    }

    public YamlConfiguration config;

    public String getIdentifier() { return "FACTION_TOP"; }
    protected RPFeature getFeature() { return getFactionTop(); }
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Faction Top &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }
}
