package me.randomhashtags.randompackage.supported;

import me.randomhashtags.randompackage.util.RPFeatureSpigot;

public enum EconomyAPI implements RPFeatureSpigot {
    INSTANCE;

    private boolean vault;

    @Override
    public String getIdentifier() {
        return "ECONOMY_API";
    }
    public void load() {
    }
    public void unload() {
    }
}
