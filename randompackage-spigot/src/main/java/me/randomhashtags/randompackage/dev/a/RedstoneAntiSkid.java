package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.util.RPFeatureSpigot;

public enum RedstoneAntiSkid implements RPFeatureSpigot {
    INSTANCE;

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&aLoaded Redstone Anti Skid &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
    }
}
