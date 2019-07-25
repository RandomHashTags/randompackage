package me.randomhashtags.randompackage.addons.utils;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Applyable extends Itemable{
    public abstract String getApplied();
    public abstract List<String> getAppliesTo();
    public abstract boolean canBeApplied(ItemStack is);
}
