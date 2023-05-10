package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Applyable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface TransmogScroll extends Scroll, Applyable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "transmog_scroll" };
    }
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        TransmogScroll t = getTransmogScroll(originalInput.contains(":") ? originalInput.split(":")[1] : "REGULAR");
        if(t == null) {
        }
        return t != null ? t.getItem() : null;
    }

    List<String> getRarityOrganization();
}
