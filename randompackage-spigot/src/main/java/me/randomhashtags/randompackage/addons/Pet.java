package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;

import java.util.TreeMap;

public abstract class Pet extends Itemable {
    public abstract TreeMap<Integer, Long> getCooldownForLevel();
    public abstract TreeMap<Integer, Long> getRequiredXpForLevel();
}
