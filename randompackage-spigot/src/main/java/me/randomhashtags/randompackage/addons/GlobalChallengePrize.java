package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;

public abstract class GlobalChallengePrize extends Itemable {
    public abstract int getAmount();
    public abstract int getPlacement();
    public abstract List<String> getRewards();
    public abstract LinkedHashMap<String, ItemStack> getRandomRewards();

    public static GlobalChallengePrize valueOf(int placement) {
        if(globalchallengeprizes != null) {
            for(GlobalChallengePrize p : globalchallengeprizes.values())
                if(p.getPlacement() == placement)
                    return p;
        }
        return null;
    }
    public static GlobalChallengePrize valueOf(ItemStack display) {
        if(globalchallengeprizes != null && display != null && display.hasItemMeta())
            for(GlobalChallengePrize p : globalchallengeprizes.values()) {
                final ItemStack d = p.getItem();
                if(d.isSimilar(display))
                    return p;
            }

        return null;
    }
}
