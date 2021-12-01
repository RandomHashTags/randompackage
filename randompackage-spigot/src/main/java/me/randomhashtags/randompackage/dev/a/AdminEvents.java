package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.configuration.file.YamlConfiguration;

public enum AdminEvents implements RPFeatureSpigot {
    INSTANCE;

    public YamlConfiguration config;

    @Override
    public String getIdentifier() {
        return "ADMIN_EVENTS";
    }
    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Admin Events &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
    }
}
