package me.randomhashtags.randompackage.addon.obj;

import me.randomhashtags.randompackage.addon.FactionUpgrade;
import me.randomhashtags.randompackage.addon.FactionUpgradeLevel;
import org.jetbrains.annotations.NotNull;

public final class FactionUpgradeInfo {
    private final FactionUpgrade type;
    private FactionUpgradeLevel level;
    public FactionUpgradeInfo(@NotNull FactionUpgrade type, @NotNull FactionUpgradeLevel level) {
        this.type = type;
        this.level = level;
    }
    @NotNull
    public FactionUpgrade getType() {
        return type;
    }
    @NotNull
    public FactionUpgradeLevel getLevel() {
        return level;
    }
    public void setLevel(@NotNull FactionUpgradeLevel level) {
        this.level = level;
    }
}
