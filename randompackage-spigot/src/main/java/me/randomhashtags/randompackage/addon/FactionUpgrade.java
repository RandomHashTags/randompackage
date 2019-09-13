package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.MaxLevelable;
import me.randomhashtags.randompackage.addon.util.Slotable;
import me.randomhashtags.randompackage.event.FactionUpgradeLevelupEvent;

import java.util.List;

public interface FactionUpgrade extends Itemable, MaxLevelable, Slotable {
    FactionUpgradeType getType();
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
    default int getInt(int tier, String key) {
        return getInt(tier, key, -999);
    }
    default int getInt(int tier, String key, int def) {
        final String v = getPerkValue(tier, key);
        return v != null ? Integer.parseInt(v) : def;
    }
    default double getDouble(int tier, String key) {
        return getDouble(tier, key, -999);
    }
    default double getDouble(int tier, String key, int def) {
        final String v = getPerkValue(tier, key);
        return v != null ? Double.parseDouble(v) : def;
    }

    default double getCropGrowMultiplier(int tier) { return getDouble(tier, "CropGrowMultiplier"); }
    default double getTeleportDelayMultiplier(int tier) { return getDouble(tier, "TeleportDelayMultiplier"); }
    default double getConquestBossDamageMultiplier(int tier) { return getDouble(tier, "ConquestBossDamageMultiplier"); }
    default double getCustomBossDamageMultiplier(int tier) { return getDouble(tier, "CustomBossDamageMultiplier"); }
    default int getDungeonPortalAppearanceTime(int tier) { return getInt(tier, "DungeonPortalAppearanceTime"); }
    default double getEnemyDamageMultiplier(int tier) { return getDouble(tier, "EnemyDamageMultiplier"); }
    default double getFactionMobSpawnRateMultiplier(int tier) { return getDouble(tier, "MobSpawnRateMultiplier"); }
    default double getFactionMobXpMultiplier(int tier) { return getDouble(tier, "FactionMobXpMultiplier"); }
    default double getHungerMultiplier(int tier) { return getDouble(tier, "HungerMultiplier"); }
    default double getIncomingArmorSetDamageMultiplier(int tier) { return getDouble(tier, "IncomingArmorSetDamageMultiplier"); }
    default int getIncreaseFactionPower(int tier) { return getInt(tier, "IncreaseFactionPower"); }
    default int getIncreaseFactionSize(int tier) { return getInt(tier, "IncreaseFactionSize"); }
    default int getIncreaseFactionWarps(int tier) { return getInt(tier, "IncreaseFactionWarps"); }
    default double getLastManStandingMultiplier(int tier) { return getDouble(tier, "LMSMultiplier"); }
    default double getOutpostCaptureMultiplier(int tier) { return getDouble(tier, "OutpostCapMultiplier"); }
    default double getRarityGemCostMultiplier(int tier, String rarity) { return getDouble(tier, rarity + "RarityGemCostMultiplier"); }
    default double getReduceCombatTagTime(int tier) { return getDouble(tier, "ReduceCombatTagTime"); }
    default double getReduceEnderpearlCooldown(int tier) { return getDouble(tier, "ReduceEnderpearlCooldown"); }
    default double getReduceMCMMOCooldown(int tier, String skill) { return getDouble(tier, "Reduce" + skill.toLowerCase().replace("all", "MCMMO") + "Cooldown"); }
    default double getVkitLevelingChance(int tier) { return getDouble(tier, "VkitLevelingChance"); }
}
