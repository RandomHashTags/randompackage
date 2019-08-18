package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Applyable;
import me.randomhashtags.randompackage.addons.utils.Percentable;

public interface EnchantmentOrb extends Applyable, Percentable {
    int getMaxAllowableEnchants();
    int getPercentLoreSlot();
    int getIncrement();
}
