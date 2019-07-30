package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;

public interface GlobalChallengePrize extends Itemable {
    int getAmount();
    int getPlacement();
    List<String> getRewards();
    LinkedHashMap<String, ItemStack> getRandomRewards();
}
