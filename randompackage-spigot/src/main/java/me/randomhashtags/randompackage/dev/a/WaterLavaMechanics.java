package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.util.RPFeature;
import org.bukkit.configuration.file.YamlConfiguration;

public enum WaterLavaMechanics implements RPFeature {
    INSTANCE;

    public YamlConfiguration config;

    @Override
    public String getIdentifier() {
        return "WATER/LAVA_MECHANICS";
    }
    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Water/Lava Mechanics &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
    }
}
