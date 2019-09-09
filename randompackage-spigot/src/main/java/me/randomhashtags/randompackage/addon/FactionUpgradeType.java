package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Identifiable;

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
