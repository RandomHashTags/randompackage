package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Itemable;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface RarityFireball extends Itemable {
    ItemStack getRevealedItem(boolean usesChances);
    List<EnchantRarity> getExchangeableRarities();
    List<String> getReveals();
}
