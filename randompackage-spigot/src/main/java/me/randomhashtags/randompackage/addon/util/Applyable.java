package me.randomhashtags.randompackage.addon.util;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Applyable extends Itemable {
    @NotNull String getAppliedString();
    @NotNull List<String> getAppliesTo();
    boolean canBeApplied(@NotNull ItemStack is);
}
