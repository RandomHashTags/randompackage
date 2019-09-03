package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addons.utils.Attributable;
import me.randomhashtags.randompackage.addons.utils.Itemable;
import me.randomhashtags.randompackage.addons.utils.Scheduleable;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.List;

public interface Dungeon extends Attributable, Itemable, Scheduleable {
    int getSlot();
    ItemStack getKey();
    ItemStack getKeyLocked();
    ItemStack getPortal();
    Recipe getPortalRecipe();
    Location getTeleportLocation();
    List<String> getAllowedCommands();
    List<DungeonBoss> getBosses();

    ItemStack getLootbag();
    List<String> getLootbagRewards();

    long getFastestCompletion();
}
