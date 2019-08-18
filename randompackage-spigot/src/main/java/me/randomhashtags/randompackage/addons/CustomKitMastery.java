package me.randomhashtags.randompackage.addons;

import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;

public interface CustomKitMastery extends CustomKit {
    ItemStack getRedeem();
    LinkedHashMap<CustomKit, Integer> getRequiredKits();
    boolean losesRequiredKits();
    ItemStack getShard();
    ItemStack getAntiCrystal();
    ItemStack getAntiCrystal(int percent);
    List<String> getAntiCrystalNegatedEnchants();
    List<String> getAntiCrystalApplied();
}
