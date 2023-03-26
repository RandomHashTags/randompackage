package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Nameable;
import me.randomhashtags.randompackage.api.ItemSkins;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ItemSkin extends Nameable, Attributable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "itemskin" };
    }
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        final ItemSkin skin = getItemSkin(originalInput.split(":")[1]);
        final ItemStack target = ItemSkins.INSTANCE.getItemSkinItem(skin, true);
        return target != null ? target : AIR;
    }

    String getMaterial();
    List<String> getLore();
}
