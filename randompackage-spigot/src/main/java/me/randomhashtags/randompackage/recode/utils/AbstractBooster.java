package me.randomhashtags.randompackage.recode.utils;

import org.bukkit.inventory.ItemStack;

public abstract class AbstractBooster extends AbstractRPFeature {
    public abstract String getType();
    public abstract ItemStack getItem();
    public abstract ItemStack getItem(long duration, double multiplier);
    public abstract int getDurationLoreSlot();
    public abstract int getMultiplierLoreSlot();
}
