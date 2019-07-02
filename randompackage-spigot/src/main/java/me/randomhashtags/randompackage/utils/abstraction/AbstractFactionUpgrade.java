package me.randomhashtags.randompackage.utils.abstraction;

import me.randomhashtags.randompackage.api.events.FactionUpgradeLevelupEvent;
import me.randomhashtags.randompackage.utils.AbstractRPFeature;
import me.randomhashtags.randompackage.utils.NamespacedKey;
import me.randomhashtags.randompackage.utils.classes.FactionUpgradeType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractFactionUpgrade extends AbstractRPFeature {
    public static HashMap<NamespacedKey, AbstractFactionUpgrade> upgrades;

    public void created(NamespacedKey key) {
        if(upgrades == null) upgrades = new HashMap<>();
        upgrades.put(key, this);
    }
    public abstract ItemStack getItem();
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

    public static AbstractFactionUpgrade valueOf(int slot) {
        if(upgrades != null) {
            for(AbstractFactionUpgrade f : upgrades.values()) {
                if(f.getSlot() == slot) {
                    return f;
                }
            }
        }
        return null;
    }
}
