package me.randomhashtags.randompackage.recode.api.addons;

import me.randomhashtags.randompackage.recode.RPAddon;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Dungeon extends RPAddon {

    public abstract ItemStack getDisplay();
    public abstract ItemStack getKey();
    public abstract ItemStack getKeyLocked();
    public abstract ItemStack getLootbag();
    public abstract List<String> getLootbagRewards();
    public abstract int getSlot();
    public abstract Location getTeleportLocation();
    public abstract long getFastestCompletion();

    public static Dungeon valueOf(ItemStack key) {
        if(dungeons != null && key != null) {
            for(Dungeon d : dungeons.values()) {
                if(d.getKey().isSimilar(key)) {
                    return d;
                }
            }
        }
        return null;
    }
}
