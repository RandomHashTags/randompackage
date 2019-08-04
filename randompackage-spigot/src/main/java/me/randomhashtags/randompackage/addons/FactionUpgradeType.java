package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Identifiable;

import java.util.List;

public interface FactionUpgradeType extends Identifiable {
    String getPerkAchievedPrefix();
    String getPerkUnachievedPrefix();
    String getRequirementsPrefix();
    List<String> getUnlock();
    List<String> getUpgrade();
    List<String> getMaxed();
    List<String> getFormat();
}
