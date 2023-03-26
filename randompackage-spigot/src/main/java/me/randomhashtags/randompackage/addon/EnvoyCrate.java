package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.Placeable;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface EnvoyCrate extends Itemable, Placeable {
    @Nullable Firework getFirework();
    int getChance();
    boolean canRepeatRewards();
    @NotNull String getRewardSize();
    @NotNull List<String> getRandomRewards();
    @NotNull List<ItemStack> getRandomizedRewards();
}
