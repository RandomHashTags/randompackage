package me.randomhashtags.randompackage.utils.abstraction;

import me.randomhashtags.randompackage.utils.AbstractRPFeature;
import me.randomhashtags.randompackage.utils.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.util.TreeMap;

public abstract class AbstractBooster extends AbstractRPFeature {
    public static TreeMap<NamespacedKey, AbstractBooster> boosters;

    public void created(NamespacedKey key) {
        if(boosters == null) boosters = new TreeMap<>();
        boosters.put(key, this);
    }

    public abstract String getType();
    public abstract ItemStack getItem();
    public abstract ItemStack getItem(long duration, double multiplier);
    public abstract int getDurationLoreSlot();
    public abstract int getMultiplierLoreSlot();

    public static AbstractBooster valueOf(String key) {
        if(boosters != null) {
            for(NamespacedKey k : boosters.keySet()) {
                if(k.key.equals(key)) {
                    return boosters.get(k);
                }
            }
        }
        return null;
    }
}