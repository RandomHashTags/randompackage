package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.util.RPFeature;

public enum RedstoneAntiSkid implements RPFeature {
    INSTANCE;

    @Override
    public String getIdentifier() {
        return "REDSTONE_ANTI_SKID";
    }

    @Override
    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Redstone Anti Skid &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    @Override
    public void unload() {
    }
}
