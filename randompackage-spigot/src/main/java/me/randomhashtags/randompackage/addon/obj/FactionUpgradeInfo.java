package me.randomhashtags.randompackage.addon.obj;

import me.randomhashtags.randompackage.addon.FactionUpgrade;
import me.randomhashtags.randompackage.addon.FactionUpgradeLevel;

public final class FactionUpgradeInfo {
    private final FactionUpgrade type;
    private FactionUpgradeLevel level;
    public FactionUpgradeInfo(FactionUpgrade type, FactionUpgradeLevel level) {
        this.type = type;
        this.level = level;
    }
    public FactionUpgrade getType() {
        return type;
    }
    public FactionUpgradeLevel getLevel() {
        return level;
    }
    public void setLevel(FactionUpgradeLevel level) {
        this.level = level;
    }
}
