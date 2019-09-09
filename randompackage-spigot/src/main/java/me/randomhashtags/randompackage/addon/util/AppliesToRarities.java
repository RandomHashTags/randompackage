package me.randomhashtags.randompackage.addon.util;

import me.randomhashtags.randompackage.addon.EnchantRarity;

import java.util.List;

public interface AppliesToRarities extends Identifiable {
    List<EnchantRarity> getAppliesToRarities();
}
