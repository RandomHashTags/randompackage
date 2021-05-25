package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Bounty implements RPFeature {
    INSTANCE;

    public YamlConfiguration config;

    @Override
    public String getIdentifier() {
        return "BOUNTY";
    }

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Bounty &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
    }
}
