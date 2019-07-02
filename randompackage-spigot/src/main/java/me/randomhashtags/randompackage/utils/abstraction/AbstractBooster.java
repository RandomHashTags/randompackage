package me.randomhashtags.randompackage.utils.abstraction;

import me.randomhashtags.randompackage.utils.AbstractRPFeature;
import org.bukkit.inventory.ItemStack;

import java.util.TreeMap;

public abstract class AbstractBooster extends AbstractRPFeature {
    public static TreeMap<String, AbstractBooster> boosters;

    public void created(String identifier) {
        if(boosters == null) boosters = new TreeMap<>();
        boosters.put(identifier, this);
    }
    public abstract String getType();
    public abstract ItemStack getItem();
    public abstract ItemStack getItem(long duration, double multiplier);
    public abstract int getDurationLoreSlot();
    public abstract int getMultiplierLoreSlot();

    public static AbstractBooster valueOf(String key) {
        if(boosters != null) {
            for(String i : boosters.keySet()) {
                if(i.equals(key)) {
                    return boosters.get(i);
                }
            }
        }
        return null;
    }
}