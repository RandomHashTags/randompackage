package me.randomhashtags.randompackage.supported;

import me.randomhashtags.randompackage.util.RPFeature;

public enum EconomyAPI implements RPFeature {
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
