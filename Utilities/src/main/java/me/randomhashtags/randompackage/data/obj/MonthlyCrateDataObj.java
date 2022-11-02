package me.randomhashtags.randompackage.data.obj;

import me.randomhashtags.randompackage.data.MonthlyCrateData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class MonthlyCrateDataObj implements MonthlyCrateData {
    private final HashMap<String, Boolean> owned;

    public MonthlyCrateDataObj(@NotNull HashMap<String, Boolean> owned) {
        this.owned = owned;
    }

    @NotNull
    @Override
    public HashMap<String, Boolean> getOwned() {
        return owned;
    }
}
