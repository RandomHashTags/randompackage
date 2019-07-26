package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;

import java.util.List;

public abstract class Trinket extends Itemable {
    public abstract String getSoulCostPerUse();
    public abstract String getRadius();
    public abstract String getCooldown();
    public abstract List<String> getAttributes();
}
