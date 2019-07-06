package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Identifyable;

import java.util.List;

public abstract class FactionUpgradeType extends Identifyable {
    public abstract String getPerkAchievedPrefix();
    public abstract String getPerkUnachievedPrefix();
    public abstract String getRequirementsPrefix();
    public abstract List<String> getUnlock();
    public abstract List<String> getUpgrade();
    public abstract List<String> getMaxed();
    public abstract List<String> getFormat();
}
