package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Dungeon extends Itemable {
    ItemStack getKey();
    ItemStack getKeyLocked();
    ItemStack getLootbag();
    List<String> getLootbagRewards();
    int getSlot();
    Location getTeleportLocation();
    long getFastestCompletion();
}
