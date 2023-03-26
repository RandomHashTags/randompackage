package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.AppliesToRarities;
import me.randomhashtags.randompackage.addon.util.Percentable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface BlackScroll extends Scroll, AppliesToRarities, Percentable, GivedpItemableSpigot {
    default String[] getGivedpItemIdentifiers() {
        return new String[] { "blackscroll" };
    }
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        final String[] values = originalInput.split(":");
        final BlackScroll scroll = getBlackScroll(values[1]);
        int amount = 0;
        if(scroll != null) {
            amount = values.length == 3 ? getIntegerFromString(values[2], scroll.getMinPercent()) : scroll.getRandomPercent(RANDOM);
        }
        return scroll != null ? scroll.getItem(amount) : null;
    }
}
