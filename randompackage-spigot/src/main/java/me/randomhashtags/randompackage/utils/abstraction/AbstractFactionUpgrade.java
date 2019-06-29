package me.randomhashtags.randompackage.utils.abstraction;

import me.randomhashtags.randompackage.utils.classes.FactionUpgradeType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public abstract class AbstractFactionUpgrade extends Saveable {

    private ItemStack item;

    public FactionUpgradeType getType() { return FactionUpgradeType.types.getOrDefault(yml.getString("settings.type"), null); }
    public int getSlot() { return yml.getInt("settings.slot"); }
    public int getMaxTier() { return yml.getInt("settings.max tier"); }
    public boolean itemAmountEqualsTier() { return yml.getBoolean("settings.item amount=tier"); }
    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "item");
        return item.clone();
    }
    public List<String> getPerks() { return yml.getStringList("perks"); }
    public List<String> getRequirements() { return yml.getStringList("requirements"); }

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
        final String v = getPerkValue(tier, "CropGrowthMultiplier");
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
}
