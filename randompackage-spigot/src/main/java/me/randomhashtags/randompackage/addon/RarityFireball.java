package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Itemable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface RarityFireball extends Itemable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "rarityfireball" };
    }
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        final RarityFireball f = getRarityFireball(originalInput.split(":")[1]);
        return f != null ? f.getItem() : null;
    }

    ItemStack getRevealedItem(boolean usesChances);
    List<EnchantRarity> getExchangeableRarities();
    List<String> getReveals();
}
