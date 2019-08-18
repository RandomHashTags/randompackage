package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import me.randomhashtags.randompackage.addons.utils.Rewardable;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;

public interface GlobalChallengePrize extends Itemable, Rewardable {
    int getAmount();
    int getPlacement();
    LinkedHashMap<String, ItemStack> getRandomRewards();
}
