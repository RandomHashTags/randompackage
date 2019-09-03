package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addons.utils.Attributable;
import me.randomhashtags.randompackage.addons.utils.Itemable;

public interface Trinket extends Attributable, Itemable {
    String getSoulCostPerUse();
    String getRadius();
    String getCooldown();
}
