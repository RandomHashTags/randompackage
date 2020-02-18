package me.randomhashtags.randompackage.data;

import me.randomhashtags.randompackage.NotNull;
import me.randomhashtags.randompackage.addon.MonthlyCrate;

import java.util.HashMap;

public interface MonthlyCrateData {
    HashMap<MonthlyCrate, Boolean> getOwned();
    default boolean isClaimed(@NotNull MonthlyCrate crate) {
        final HashMap<MonthlyCrate, Boolean> owned = getOwned();
        return owned != null && owned.containsKey(crate) && owned.get(crate);
    }
}
