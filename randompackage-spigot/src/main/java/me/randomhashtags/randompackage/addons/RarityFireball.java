package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface RarityFireball extends Itemable {
    ItemStack getRevealedItem(boolean usesChances);
    List<EnchantRarity> getExchangeableRarities();
    List<String> getReveals();
}
