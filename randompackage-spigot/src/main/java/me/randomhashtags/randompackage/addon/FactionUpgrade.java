package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.MaxLevelable;
import me.randomhashtags.randompackage.addon.util.Slotable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;

public interface FactionUpgrade extends Itemable, MaxLevelable, Slotable, Attributable {
    @NotNull FactionUpgradeType getType();
    boolean itemAmountEqualsTier();
    @NotNull LinkedHashMap<Integer, FactionUpgradeLevel> getLevels();
    default int getMaxLevel() {
        final LinkedHashMap<Integer, FactionUpgradeLevel> levels = getLevels();
        return (int) levels.keySet().toArray()[levels.size()-1];
    }
}
