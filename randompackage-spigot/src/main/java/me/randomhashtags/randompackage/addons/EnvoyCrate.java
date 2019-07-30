package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface EnvoyCrate extends Itemable {
    Firework getFirework();
    int getChance();
    UMaterial getBlock();
    boolean canRepeatRewards();
    boolean dropsFromSky();
    UMaterial getFallingBlock();
    String getRewardSize();
    List<UMaterial> cannotLandAbove();
    List<String> getRewards();
    List<String> getRandomRewards();
    List<ItemStack> getRandomizedRewards();
    boolean canLand(Location l);
}
