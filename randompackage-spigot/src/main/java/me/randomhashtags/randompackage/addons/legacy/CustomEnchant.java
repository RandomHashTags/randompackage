package me.randomhashtags.randompackage.addons.legacy;

import me.randomhashtags.randompackage.addons.utils.Identifiable;
import me.randomhashtags.randompackage.utils.addons.RPAddon;

import java.util.List;

public abstract class CustomEnchant extends RPAddon implements Identifiable {
    public abstract boolean isEnabled();
    public abstract String getName();
    public abstract List<String> getLore();
    public abstract int getMaxLevel();
    public abstract List<String> getAppliesTo();
    public abstract String getRequiredEnchant();
    public abstract int[] getAlchemist();
    public abstract int getAlchemistUpgradeCost(int level);
    public abstract int[] getTinkerer();
    public abstract int getTinkererValue(int level);
    public abstract String getEnchantProcValue();
    public abstract List<String> getAttributes();
}
