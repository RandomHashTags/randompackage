package me.randomhashtags.randompackage.data;

import com.sun.istack.internal.NotNull;

import java.util.HashMap;

public interface MonthlyCrateData {
    HashMap<String, Boolean> getOwned();
    default boolean isClaimed(@NotNull String identifier) {
        final HashMap<String, Boolean> owned = getOwned();
        return owned != null && owned.containsKey(identifier) && owned.get(identifier);
    }
}
