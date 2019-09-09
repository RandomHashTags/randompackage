package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.Rewardable;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;

public interface GlobalChallengePrize extends Itemable, Rewardable {
    int getAmount();
    int getPlacement();
    LinkedHashMap<String, ItemStack> getRandomRewards();
}
