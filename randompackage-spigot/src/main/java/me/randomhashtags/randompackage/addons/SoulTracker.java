package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Applyable;

import java.util.List;

public abstract class SoulTracker extends Applyable {
    public abstract String getTracks();
    public abstract List<String> getAppliesTo();
    public abstract String getSoulsPerKill();
    public abstract double getSoulsCollected();
    public abstract RarityGem getConvertsTo();
    public abstract List<String> getApplyMsg();
    public abstract List<String> getSplitMsg();


}
