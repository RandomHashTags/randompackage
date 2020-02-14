package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Nameable;
import me.randomhashtags.randompackage.api.ItemSkins;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface ItemSkin extends Nameable, Attributable, GivedpItemable {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "itemskin" };
    }
    default ItemStack valueOfInput(String originalInput, String lowercaseInput) {
        final ItemSkin skin = getItemSkin(originalInput.split(":")[1]);
        final ItemStack target = ItemSkins.getItemSkins().getItemSkinItem(skin, true);
        return target != null ? target : AIR;
    }

    String getMaterial();
    List<String> getLore();
}
