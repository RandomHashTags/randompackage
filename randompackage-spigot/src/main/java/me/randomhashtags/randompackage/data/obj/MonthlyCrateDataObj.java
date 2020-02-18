package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.MonthlyCrateData;

import java.util.HashMap;

public class MonthlyCrateDataObj implements MonthlyCrateData {
    private HashMap<String, Boolean> owned;

    public MonthlyCrateDataObj(HashMap<String, Boolean> owned) {
        this.owned = owned;
    }

    @Override
    public HashMap<String, Boolean> getOwned() {
        return owned;
    }
}
