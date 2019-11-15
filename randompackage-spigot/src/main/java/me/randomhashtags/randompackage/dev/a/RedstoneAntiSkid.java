package me.randomhashtags.randompackage.dev.a;

import me.randomhashtags.randompackage.util.RPFeature;

public class RedstoneAntiSkid extends RPFeature {
    private static RedstoneAntiSkid instance;
    public static RedstoneAntiSkid getRedstoneAntiSkid() {
        if(instance == null) instance = new RedstoneAntiSkid();
        return instance;
    }

    public String getIdentifier() { return "REDSTONE_ANTI_SKID"; }

    public void load() {
        final long started = System.currentTimeMillis();
        sendConsoleMessage("&6[RandomPackage] &aLoaded Redstone Anti Skid &e(took " + (System.currentTimeMillis()-started) + "ms)");
    }
    public void unload() {
    }
}
