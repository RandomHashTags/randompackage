package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Applyable;
import me.randomhashtags.randompackage.addon.util.Percentable;

public interface EnchantmentOrb extends Applyable, Percentable {
    int getMaxAllowableEnchants();
    int getPercentLoreSlot();
    int getIncrement();
}
