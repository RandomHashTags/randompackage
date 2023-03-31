package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.Toggleable;
import me.randomhashtags.randompackage.util.RPItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public interface Trinket extends Itemable, Attributable, Toggleable, RPItemStack, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "trinket" };
    }
    @Nullable
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        Trinket trinket = getTrinket(originalInput.contains(":") ? originalInput.split(":")[1] : "random");
        if(trinket == null) {
        }
        return trinket != null ? trinket.getItem() : null;
    }

    boolean isPassive();
    long getCooldown();

    @NotNull
    default ItemStack getItem(long cooldownExpiration) {
        final ItemStack i = getItem();
        setItem(i, getIdentifier(), cooldownExpiration);
        return i;
    }
    default void setItem(ItemStack is, String identifier, long cooldownExpiration) {
        addRPItemStackValues(is, new HashMap<>() {{
            put("TrinketInfo", identifier + ":" + cooldownExpiration);
        }});
    }
    default void didUse(ItemStack is, String identifier) {
        setItem(is, identifier, System.currentTimeMillis() + (getCooldown()*1000));
    }
}
