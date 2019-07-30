package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;

import java.util.List;

public interface RandomizationScroll extends Itemable {
    List<EnchantRarity> getAppliesToRarities();
}
