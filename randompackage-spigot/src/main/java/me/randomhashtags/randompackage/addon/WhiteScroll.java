package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Applyable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface WhiteScroll extends Scroll, Applyable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "white_scroll" };
    }
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        WhiteScroll scroll = getWhiteScroll(originalInput.contains(":") ? originalInput.split(":")[1] : "REGULAR");
        if(scroll == null) {
        }
        return scroll != null ? scroll.getItem() : null;
    }

    @Nullable String getRequiredWhiteScroll();
    boolean removesRequiredAfterApplication();
}
