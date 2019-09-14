package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.Toggleable;
import me.randomhashtags.randompackage.util.RPItemStack;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface Trinket extends Itemable, Attributable, Toggleable, RPItemStack {
    HashMap<String, String> getSettings();
    default String getSetting(String identifier) {
        return getSettings().getOrDefault(identifier, "");
    }

    default long getCooldownExpiration(ItemStack is) {
        final String s = getRPItemStackValue(is, "TrinketInfo");
        return s != null ? Long.parseLong(s.split(":")[1]) : -1;
    }

    default void setItem(ItemStack is, String identifier, long cooldownExpiration) {
        setRPItemStackValues(is, new HashMap<String, String>() {{
            put("TrinketInfo", identifier + ":" + cooldownExpiration);
        }});
    }
    default void didUse(ItemStack is, String identifier) {
        setItem(is, identifier, System.currentTimeMillis()+Long.parseLong(getSetting("cooldown")));
    }
}
