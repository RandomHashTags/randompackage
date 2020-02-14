package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.obj.RandomizedLootItem;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.RewardSizeable;
import me.randomhashtags.randompackage.addon.util.Rewardable;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface RandomizedLoot extends Itemable, Rewardable, RewardSizeable, GivedpItemable {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "randomizedloot" };
    }
    default ItemStack valueOfInput(String originalInput, String lowercaseInput) {
        final me.randomhashtags.randompackage.api.RandomizedLoot loot = me.randomhashtags.randompackage.api.RandomizedLoot.getRandomizedLoot();
        final HashMap<String, RandomizedLootItem> items = loot.isEnabled() ? loot.items : null;
        final ItemStack target = items != null && items.containsKey(originalInput) ? items.get(originalInput).getItem() : null;
        return target != null ? target : AIR;
    }

}
