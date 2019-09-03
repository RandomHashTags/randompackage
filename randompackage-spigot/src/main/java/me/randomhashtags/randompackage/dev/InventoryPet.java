package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addons.utils.Attributable;
import me.randomhashtags.randompackage.addons.utils.Itemable;

import java.util.TreeMap;

public interface InventoryPet extends Itemable, Attributable {
    TreeMap<Integer, Long> getCooldowns();
    TreeMap<Integer, Long> getRequiredXp();
    int getCooldownSlot();
    int getExpSlot();
}
