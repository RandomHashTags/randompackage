package me.randomhashtags.randompackage.addons.utils;

import me.randomhashtags.randompackage.addons.EnchantRarity;

import java.util.List;

public interface AppliesToRarities extends Identifiable {
    List<EnchantRarity> getAppliesToRarities();
}
