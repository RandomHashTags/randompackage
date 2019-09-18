package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.MaxLevelable;
import me.randomhashtags.randompackage.addon.util.Slotable;

import java.util.HashMap;

public interface FactionUpgrade extends Itemable, MaxLevelable, Slotable, Attributable {
    FactionUpgradeType getType();
    boolean itemAmountEqualsTier();
    HashMap<Integer, FactionUpgradeLevel> getLevels();
}
