package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Applyable;
import me.randomhashtags.randompackage.addon.util.Attributable;
import org.bukkit.inventory.ItemStack;

public interface Mask extends Applyable, Attributable, GivedpItemable {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "mask", "multimask" };
    }
    default ItemStack valueOfInput(String originalInput, String lowercaseInput) {
        final boolean isMulti = lowercaseInput.startsWith("multi");
        final Mask mask = getMask(originalInput.split(":")[1]);
        final ItemStack target = mask != null ? mask.getItem() : null;
        return target != null ? isMulti ? AIR : target : AIR;
    }
}
