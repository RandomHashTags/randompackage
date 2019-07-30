package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Identifyable;

import java.util.List;

public interface FactionUpgradeType extends Identifyable {
    String getPerkAchievedPrefix();
    String getPerkUnachievedPrefix();
    String getRequirementsPrefix();
    List<String> getUnlock();
    List<String> getUpgrade();
    List<String> getMaxed();
    List<String> getFormat();
}
