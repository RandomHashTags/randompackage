package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.bukkit.inventory.ItemStack;

public abstract class Booster extends Itemable {
    public abstract String getType();
    public abstract ItemStack getItem(long duration, double multiplier);
    public abstract int getDurationLoreSlot();
    public abstract int getMultiplierLoreSlot();
}
