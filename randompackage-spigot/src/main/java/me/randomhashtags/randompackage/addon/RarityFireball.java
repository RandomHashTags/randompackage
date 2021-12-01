package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Itemable;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface RarityFireball extends Itemable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "rarityfireball" };
    }
    default ItemStack valueOfInput(String originalInput, String lowercaseInput) {
        final RarityFireball f = getRarityFireball(originalInput.split(":")[1]);
        final ItemStack target = f != null ? f.getItem() : null;
        return target != null ? target : AIR;
    }

    ItemStack getRevealedItem(boolean usesChances);
    List<EnchantRarity> getExchangeableRarities();
    List<String> getReveals();
}
