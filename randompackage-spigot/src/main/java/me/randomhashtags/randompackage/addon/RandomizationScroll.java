package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.AppliesToRarities;
import me.randomhashtags.randompackage.addon.util.Itemable;
import org.bukkit.inventory.ItemStack;

public interface RandomizationScroll extends Scroll, AppliesToRarities, Itemable, GivedpItemable {
    default String[] getGivedpItemIdentifiers() {
        return new String[] { "randomizationscroll" };
    }
    default ItemStack valueOfInput(String originalInput, String lowercaseInput) {
        final RandomizationScroll scroll = getRandomizationScroll(originalInput.split(":")[1]);
        final ItemStack target = scroll != null ? scroll.getItem() : null;
        return target != null ? target : AIR;
    }
}
