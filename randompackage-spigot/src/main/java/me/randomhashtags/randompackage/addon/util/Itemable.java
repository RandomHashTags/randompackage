package me.randomhashtags.randompackage.addon.util;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface Itemable extends Identifiable {
    @NotNull ItemStack getItem();
}
