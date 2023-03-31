package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.util.RPFeatureSpigot;
import org.bukkit.configuration.file.YamlConfiguration;

public enum AdminEvents implements RPFeatureSpigot {
    INSTANCE;

    public YamlConfiguration config;

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&aLoaded Admin Events &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
    }
}
