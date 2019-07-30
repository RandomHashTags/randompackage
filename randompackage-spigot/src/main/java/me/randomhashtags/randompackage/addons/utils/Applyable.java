package me.randomhashtags.randompackage.addons.utils;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Applyable extends Itemable {
    String getApplied();
    List<String> getAppliesTo();
    boolean canBeApplied(ItemStack is);
}
