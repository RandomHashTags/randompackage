package me.randomhashtags.randompackage.utils.supported;

import me.randomhashtags.randompackage.utils.RPFeature;

public class EconomyAPI extends RPFeature {
    private static EconomyAPI instance;
    public static EconomyAPI getEconomyAPI() {
        if(instance == null) instance = new EconomyAPI();
        return instance;
    }

    private boolean vault;

    public String getIdentifier() { return "ECONOMY_API"; }
    protected RPFeature getFeature() { return getEconomyAPI(); }
    public void load() {
    }
    public void unload() {
    }
}
