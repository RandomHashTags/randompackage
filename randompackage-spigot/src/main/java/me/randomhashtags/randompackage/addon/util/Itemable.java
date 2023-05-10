package me.randomhashtags.randompackage.addon.util;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Itemable extends Identifiable {
    @Nullable ItemStack getItem();
}
