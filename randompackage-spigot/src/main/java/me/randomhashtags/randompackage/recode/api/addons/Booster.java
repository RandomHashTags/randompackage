package me.randomhashtags.randompackage.recode.api.addons;

import me.randomhashtags.randompackage.recode.RPAddon;
import org.bukkit.inventory.ItemStack;

public abstract class Booster extends RPAddon {
    public abstract String getType();
    public abstract ItemStack getItem();
    public abstract ItemStack getItem(long duration, double multiplier);
    public abstract int getDurationLoreSlot();
    public abstract int getMultiplierLoreSlot();
}
