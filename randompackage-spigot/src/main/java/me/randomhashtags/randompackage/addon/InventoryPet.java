package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.*;
import me.randomhashtags.randompackage.api.dev.InventoryPets;
import me.randomhashtags.randompackage.universal.UVersionable;
import me.randomhashtags.randompackage.util.RPItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public interface InventoryPet extends UVersionable, Itemable, Attributable, Skullable, MaxLevelable, Toggleable, RPItemStack {
    HashMap<Integer, Integer> getCooldowns();
    default long getCooldown(int level) {
        final HashMap<Integer, Integer> cooldowns = getCooldowns();
        return cooldowns.getOrDefault(-1, cooldowns.getOrDefault(level, 3600));
    }
    default long getCooldown(ItemStack is) {
        final String v = getRPItemStackValue(is, "InventoryPetInfo");
        return v != null ? Long.parseLong(v.split(":")[3]) : -1;
    }

    HashMap<Integer, Integer> getRequiredXp();
    default int getRequiredXp(int level) {
        final HashMap<Integer, Integer> r = getRequiredXp();
        return r.getOrDefault(-1, r.getOrDefault(level, 1000));
    }

    default ItemStack getItem(int level) {
        return getItem(level, getRequiredXp(level));
    }
    default ItemStack getItem(int level, int exp) {
        return getItem(level, exp, System.currentTimeMillis()+getCooldown(level));
    }
    default ItemStack getItem(int level, int exp, long cooldownExpiration) {
        final int required = getRequiredXp(level+1);
        final String lvl = Integer.toString(level), xp = Integer.toString(exp), requiredString = formatInt(required), cooldown = getRemainingTime(getCooldown(level));
        final ItemStack is = getItem();
        if(is != null) {
            final ItemMeta meta = is.getItemMeta();
            meta.setDisplayName(meta.getDisplayName().replace("{LEVEL}", lvl));
            final List<String> lore = new ArrayList<>();
            final String expRegex = InventoryPets.getInventoryPets().getExpRegex(exp, required);
            if(meta.hasLore()) {
                for(String s : meta.getLore()) {
                    lore.add(s.replace("{EXP}", expRegex).replace("{LEVEL}", lvl).replace("{XP}", xp).replace("{COMPLETION}", requiredString).replace("{COOLDOWN}", cooldown));
                }
            }
            meta.setLore(lore);
            is.setItemMeta(meta);
            setItem(is, getIdentifier(), level, exp, cooldownExpiration);
        }
        return is;
    }

    default void setItem(ItemStack is, String identifier, int level, int exp, long cooldownExpiration) {
        setRPItemStackValues(is, new HashMap<String, String>() {{
            put("InventoryPetInfo", identifier + ":" + level + ":" + exp + ":" + cooldownExpiration);
        }});
    }

    default void didUse(ItemStack is, String identifier, int level, int exp) {
        setItem(is, identifier, level, exp, System.currentTimeMillis()+getCooldown(level));
    }

    ItemStack getEgg();
    LinkedHashMap<InventoryPet, Integer> getEggRequiredPets();
}
