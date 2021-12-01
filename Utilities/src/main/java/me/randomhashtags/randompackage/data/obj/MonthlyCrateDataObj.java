package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.MonthlyCrateData;

import java.util.HashMap;

public final class MonthlyCrateDataObj implements MonthlyCrateData {
    private final HashMap<String, Boolean> owned;

    public MonthlyCrateDataObj(HashMap<String, Boolean> owned) {
        this.owned = owned;
    }

    @Override
    public HashMap<String, Boolean> getOwned() {
        return owned;
    }
}
