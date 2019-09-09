package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Itemable;

public interface Trinket extends Attributable, Itemable {
    String getSoulCostPerUse();
    String getRadius();
    String getCooldown();
}
