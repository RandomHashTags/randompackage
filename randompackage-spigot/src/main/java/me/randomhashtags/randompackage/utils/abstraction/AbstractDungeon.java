package me.randomhashtags.randompackage.utils.abstraction;

import me.randomhashtags.randompackage.utils.AbstractRPFeature;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractDungeon extends AbstractRPFeature {
    public static HashMap<String, AbstractDungeon> dungeons;

    public void created(String identifier) {
        if(dungeons == null) dungeons = new HashMap<>();
        dungeons.put(identifier, this);
    }
    public abstract ItemStack getDisplay();
    public abstract ItemStack getKey();
    public abstract ItemStack getKeyLocked();
    public abstract ItemStack getLootbag();
    public abstract List<String> getLootbagRewards();
    public abstract int getSlot();
    public abstract Location getTeleportLocation();
    public abstract long getFastestCompletion();

    public static AbstractDungeon valueOf(ItemStack key) {
        if(dungeons != null && key != null) {
            for(AbstractDungeon d : dungeons.values()) {
                if(d.getKey().isSimilar(key)) {
                    return d;
                }
            }
        }
        return null;
    }
}
