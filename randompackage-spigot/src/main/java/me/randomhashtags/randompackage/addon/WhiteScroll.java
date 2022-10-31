package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Applyable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public interface WhiteScroll extends Scroll, Applyable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "whitescroll" };
    }
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        WhiteScroll scroll = getWhiteScroll(originalInput.contains(":") ? originalInput.split(":")[1] : "REGULAR");
        if(scroll == null) {
        }
        final ItemStack target = scroll != null ? scroll.getItem() : null;
        return target != null ? target : AIR;
    }

    String getRequiredWhiteScroll();
    boolean removesRequiredAfterApplication();
}
