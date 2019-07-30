package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Applyable;

import java.util.List;

public interface SoulTracker extends Applyable {
    String getTracks();
    List<String> getAppliesTo();
    String getSoulsPerKill();
    double getSoulsCollected();
    RarityGem getConvertsTo();
    List<String> getApplyMsg();
    List<String> getSplitMsg();
}
