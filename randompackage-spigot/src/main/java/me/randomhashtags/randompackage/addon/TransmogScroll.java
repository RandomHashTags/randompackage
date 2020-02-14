package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Applyable;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface TransmogScroll extends Scroll, Applyable, GivedpItemable {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "transmogscroll" };
    }
    default ItemStack valueOfInput(String originalInput, String lowercaseInput) {
        TransmogScroll t = getTransmogScroll(originalInput.contains(":") ? originalInput.split(":")[1] : "REGULAR");
        if(t == null) {
        }
        final ItemStack target = t != null ? t.getItem() : null;
        return target != null ? target : AIR;
    }

    List<String> getRarityOrganization();
}
