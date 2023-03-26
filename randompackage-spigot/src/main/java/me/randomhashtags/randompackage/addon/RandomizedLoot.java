package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.obj.RandomizedLootItem;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.RewardSizeable;
import me.randomhashtags.randompackage.addon.util.Rewardable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface RandomizedLoot extends Itemable, Rewardable, RewardSizeable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "randomizedloot" };
    }
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        final me.randomhashtags.randompackage.api.RandomizedLoot loot = me.randomhashtags.randompackage.api.RandomizedLoot.INSTANCE;
        final HashMap<String, RandomizedLootItem> items = loot.isEnabled() ? loot.items : null;
        return items != null && items.containsKey(originalInput) ? items.get(originalInput).getItem() : null;
    }

}
