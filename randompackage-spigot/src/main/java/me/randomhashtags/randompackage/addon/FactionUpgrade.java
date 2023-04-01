package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.*;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

public interface FactionUpgrade extends Itemable, MaxLevelable, Slotable, Toggleable, Attributable {
    @NotNull FactionUpgradeType getType();
    boolean itemAmountEqualsTier();
    @NotNull LinkedHashMap<Integer, FactionUpgradeLevel> getLevels();
    default int getMaxLevel() {
        final LinkedHashMap<Integer, FactionUpgradeLevel> levels = getLevels();
        return (int) levels.keySet().toArray()[levels.size()-1];
    }
}
