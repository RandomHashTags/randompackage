package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Applyable;
import org.bukkit.inventory.ItemStack;

public interface WhiteScroll extends Scroll, Applyable, GivedpItemable {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "whitescroll" };
    }
    default ItemStack valueOfInput(String originalInput, String lowercaseInput) {
        WhiteScroll scroll = getWhiteScroll(originalInput.contains(":") ? originalInput.split(":")[1] : "REGULAR");
        if(scroll == null) {
        }
        final ItemStack target = scroll != null ? scroll.getItem() : null;
        return target != null ? target : AIR;
    }

    String getRequiredWhiteScroll();
    boolean removesRequiredAfterApplication();
}
