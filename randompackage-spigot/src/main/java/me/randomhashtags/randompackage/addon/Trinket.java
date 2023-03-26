package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.Toggleable;
import me.randomhashtags.randompackage.util.RPItemStack;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public interface Trinket extends Itemable, Attributable, Toggleable, RPItemStack, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "trinket" };
    }
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        Trinket trinket = getTrinket(originalInput.contains(":") ? originalInput.split(":")[1] : "random");
        if(trinket == null) {
        }
        return trinket != null ? trinket.getItem() : null;
    }

    HashMap<String, String> getSettings();
    default String getSetting(String identifier) {
        return getSetting(identifier, "");
    }
    default String getSetting(String identifier, String def) {
        return getSettings().getOrDefault(identifier, def);
    }
    default long getCooldown() {
        final String s = getSetting("cooldown");
        return !s.isEmpty() ? Long.parseLong(s)*1000 : 0;
    }

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
        setItem(is, identifier, System.currentTimeMillis()+getCooldown());
    }
}
