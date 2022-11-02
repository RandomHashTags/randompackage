package me.randomhashtags.randompackage.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public interface MonthlyCrateData {
    @NotNull HashMap<String, Boolean> getOwned();
    default boolean isClaimed(@NotNull String identifier) {
        final HashMap<String, Boolean> owned = getOwned();
        return owned.getOrDefault(identifier, false);
    }
}
