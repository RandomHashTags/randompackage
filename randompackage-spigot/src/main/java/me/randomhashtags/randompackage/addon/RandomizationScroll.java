package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.AppliesToRarities;
import me.randomhashtags.randompackage.addon.util.Itemable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface RandomizationScroll extends Scroll, AppliesToRarities, Itemable, GivedpItemableSpigot {
    default String[] getGivedpItemIdentifiers() {
        return new String[] { "randomization_scroll" };
    }
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        final RandomizationScroll scroll = getRandomizationScroll(originalInput.split(":")[1]);
        return scroll != null ? scroll.getItem() : null;
    }
}
