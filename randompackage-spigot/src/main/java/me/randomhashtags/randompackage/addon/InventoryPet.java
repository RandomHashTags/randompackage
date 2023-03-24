package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.*;
import me.randomhashtags.randompackage.api.dev.InventoryPets;
import me.randomhashtags.randompackage.util.RPItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public interface InventoryPet extends Itemable, Attributable, Skullable, MaxLevelable, Toggleable, RPItemStack, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "inventorypetegg", "petegg", "inventorypet", "pet" };
    }
    default ItemStack valueOfInput(String originalInput, String lowercaseInput) {
        final boolean isEgg = lowercaseInput.startsWith("inventorypetegg") || lowercaseInput.startsWith("petegg");
        final InventoryPet pet = getInventoryPet(originalInput.split(":")[1]);
        final ItemStack target = pet != null ? isEgg ? pet.getEgg() : pet.getItem(1) : null;
        return target != null ? target  : AIR;
    }

    HashMap<Integer, String> getValues();
    default String getValue(int level) {
        final HashMap<Integer, String> values = getValues();
        return values != null ? values.getOrDefault(level, "null") : "null";
    }

    HashMap<Integer, Integer> getCooldowns();
    default long getCooldown(int level) {
        final HashMap<Integer, Integer> cooldowns = getCooldowns();
        return cooldowns.getOrDefault(-1, cooldowns.getOrDefault(level, 3600));
    }
    default long getCooldown(@NotNull ItemStack is) {
        final String v = getRPItemStackValue(is, "InventoryPetInfo");
        return v != null ? Long.parseLong(v.split(":")[3]) : -1;
    }

    HashMap<Integer, Integer> getRequiredXp();
    default int getRequiredXp(int level) {
        final HashMap<Integer, Integer> r = getRequiredXp();
        return r.getOrDefault(-1, r.getOrDefault(level, 1000));
    }

    @NotNull
    default ItemStack getItem(int level) {
        return getItem(level, 0);
    }
    @NotNull
    default ItemStack getItem(int level, int exp) {
        return getItem(level, exp, System.currentTimeMillis()+getCooldown(level));
    }
    @NotNull
    default ItemStack getItem(int level, int exp, long cooldownExpiration) {
        final int required = getRequiredXp(level+1);
        final String lvl = Integer.toString(level), xp = Integer.toString(exp), requiredString = formatInt(required), cooldown = getRemainingTime(getCooldown(level));
        final ItemStack is = getItem();
        final ItemMeta meta = is.getItemMeta();
        meta.setDisplayName(meta.getDisplayName().replace("{LEVEL}", lvl));
        final List<String> lore = new ArrayList<>();
        final String expRegex = InventoryPets.INSTANCE.getExpRegex(exp, required);
        if(meta.hasLore()) {
            final String value = getValue(level), romanLevel = toRoman(level);
            final String[] values = value.split(";");
            for(String s : meta.getLore()) {
                int target = 1;
                for(String realValue : values) {
                    s = s.replace("{VALUE_" + target + "}", realValue);
                    target++;
                }
                lore.add(s.replace("{EXP}", expRegex).replace("{LEVEL}", lvl).replace("{ROMAN_LEVEL}", romanLevel).replace("{XP}", xp).replace("{COMPLETION}", requiredString).replace("{COOLDOWN}", cooldown).replace("{VALUE}", value));
            }
        }
        meta.setLore(lore);
        is.setItemMeta(meta);
        set_inventory_pet_values(is, getIdentifier(), level, exp, cooldownExpiration);
        return is;
    }

    default void set_inventory_pet_values(@NotNull ItemStack is, @NotNull String identifier, int level, int exp, long cooldownExpiration) {
        addRPItemStackValues(is, new HashMap<>() {{
            put("InventoryPetInfo", identifier + ":" + level + ":" + exp + ":" + cooldownExpiration);
        }});
    }

    default void didUse(ItemStack is, String identifier, int level, int exp) {
        set_inventory_pet_values(is, identifier, level, exp, System.currentTimeMillis() + getCooldown(level));
    }

    ItemStack getEgg();
    LinkedHashMap<InventoryPet, Integer> getEggRequiredPets();
}
