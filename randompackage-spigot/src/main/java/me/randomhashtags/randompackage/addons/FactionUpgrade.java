package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import me.randomhashtags.randompackage.events.FactionUpgradeLevelupEvent;

import java.util.List;

public interface FactionUpgrade extends Itemable {
    FactionUpgradeType getType();
    int getSlot();
    int getMaxTier();
    boolean itemAmountEqualsTier();
    List<String> getPerks();
    List<String> getRequirements();
    void didLevelup(FactionUpgradeLevelupEvent event);

    default String getPerkValue(int tier, String key) {
        key = key.toLowerCase();
        final String t = Integer.toString(tier);
        for(String s : getPerks()) {
            if(s.toLowerCase().replace("#", t).startsWith("tier" + t + ";" + key + "=")) {
                return s.split("=")[1].split(";")[0];
            }
        }
        return null;
    }
    default double getCropGrowMultiplier(int tier) {
        final String v = getPerkValue(tier, "CropGrowMultiplier");
        return v != null ? Double.parseDouble(v) : 1.00;
    }
    default double getTeleportDelayMultiplier(int tier) {
        final String v = getPerkValue(tier, "TeleportDelayMultiplier");
        return v != null ? Double.parseDouble(v): 1.00;
    }
    default double getCustomBossDamageMultiplier(int tier) {
        final String v = getPerkValue(tier, "CustomBossDamageMultiplier");
        return v != null ? Double.parseDouble(v) : 1.00;
    }
    default double getEnemyDamageMultiplier(int tier) {
        final String v = getPerkValue(tier, "EnemyDamageMultiplier");
        return v != null ? Double.parseDouble(v) : 1.00;
    }
    default double getRarityGemCostMultiplier(int tier) {
        final String v = getPerkValue(tier, "RarityGemCostMultiplier");
        return v != null ? Double.parseDouble(v) : 1.00;
    }
    default double getVkitLevelingChance(int tier) {
        final String v = getPerkValue(tier, "VkitLevelingChance");
        return v != null ? Double.parseDouble(v) : 1.00;
    }
}
