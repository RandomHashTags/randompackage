package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.api.events.FactionUpgradeLevelupEvent;
import me.randomhashtags.randompackage.addons.utils.Itemable;
import me.randomhashtags.randompackage.utils.RPAddon;

import java.util.List;

public abstract class FactionUpgrade extends RPAddon implements Itemable {
    public abstract FactionUpgradeType getType();
    public abstract int getSlot();
    public abstract int getMaxTier();
    public abstract boolean itemAmountEqualsTier();
    public abstract List<String> getPerks();
    public abstract List<String> getRequirements();

    public abstract void didLevelup(FactionUpgradeLevelupEvent event);

    private String getPerkValue(int tier, String key) {
        key = key.toLowerCase();
        final String t = Integer.toString(tier);
        for(String s : getPerks()) {
            if(s.toLowerCase().replace("#", t).startsWith("tier" + t + ";" + key + "=")) {
                return s.split("=")[1].split(";")[0];
            }
        }
        return null;
    }
    public double getCropGrowMultiplier(int tier) {
        final String v = getPerkValue(tier, "CropGrowMultiplier");
        return v != null ? Double.parseDouble(v) : 1.00;
    }
    public double getTeleportDelayMultiplier(int tier) {
        final String v = getPerkValue(tier, "TeleportDelayMultiplier");
        return v != null ? Double.parseDouble(v): 1.00;
    }
    public double getCustomBossDamageMultiplier(int tier) {
        final String v = getPerkValue(tier, "CustomBossDamageMultiplier");
        return v != null ? Double.parseDouble(v) : 1.00;
    }
    public double getEnemyDamageMultiplier(int tier) {
        final String v = getPerkValue(tier, "EnemyDamageMultiplier");
        return v != null ? Double.parseDouble(v) : 1.00;
    }
    public double getRarityGemCostMultiplier(int tier) {
        final String v = getPerkValue(tier, "RarityGemCostMultiplier");
        return v != null ? Double.parseDouble(v) : 1.00;
    }
    public double getVkitLevelingChance(int tier) {
        final String v = getPerkValue(tier, "VkitLevelingChance");
        return v != null ? Double.parseDouble(v) : 1.00;
    }

    public static FactionUpgrade valueOf(int slot) {
        if(factionupgrades != null) {
            for(FactionUpgrade f : factionupgrades.values()) {
                if(f.getSlot() == slot) {
                    return f;
                }
            }
        }
        return null;
    }
}
