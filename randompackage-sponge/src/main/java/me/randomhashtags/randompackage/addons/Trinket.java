package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;

import java.util.List;

public interface Trinket extends Itemable {
    String getSoulCostPerUse();
    String getRadius();
    String getCooldown();
    List<String> getAttributes();
}
