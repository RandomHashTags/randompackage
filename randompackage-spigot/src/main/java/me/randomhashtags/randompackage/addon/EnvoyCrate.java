package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.Placeable;
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
