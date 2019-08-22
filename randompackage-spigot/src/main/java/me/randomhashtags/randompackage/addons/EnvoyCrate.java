package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import me.randomhashtags.randompackage.addons.utils.Placeable;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface EnvoyCrate extends Itemable, Placeable {
    Firework getFirework();
    int getChance();
    boolean canRepeatRewards();
    String getRewardSize();
    List<String> getRandomRewards();
    List<ItemStack> getRandomizedRewards();
}
