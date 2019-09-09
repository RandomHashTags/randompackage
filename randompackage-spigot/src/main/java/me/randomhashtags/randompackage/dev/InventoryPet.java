package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Itemable;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.TreeMap;

public interface InventoryPet extends Itemable, Attributable {
    TreeMap<Integer, Long> getCooldowns();
    TreeMap<Integer, Long> getRequiredXp();
    int getCooldownSlot();
    int getExpSlot();

    ItemStack getEgg();
    LinkedHashMap<InventoryPet, Integer> getEggRequiredPets();
}
