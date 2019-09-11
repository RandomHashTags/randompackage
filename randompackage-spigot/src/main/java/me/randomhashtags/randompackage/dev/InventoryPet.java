package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.util.RPItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public interface InventoryPet extends Itemable, Attributable, RPItemStack {
    HashMap<Integer, Long> getCooldowns();
    default long getCooldown(int level) { return getCooldowns().getOrDefault(level, 0l); }
    HashMap<Integer, Long> getRequiredXp();

    int getMaxLevel();
    int getCooldownSlot();
    int getLevelSlot();
    int getExpSlot();

    default ItemStack getItem(int level, int exp) {
        final HashMap<Integer, Long> cooldowns = getCooldowns();
        final String lvl = Integer.toString(level), XP = Integer.toString(exp), xp = api.formatLong(getRequiredXp().getOrDefault(level+1, 0l)), cooldown = api.getRemainingTime(cooldowns.getOrDefault(level, 1000l));
        final ItemStack is = getItem();
        final ItemMeta m = is.getItemMeta();
        m.setDisplayName(m.getDisplayName().replace("{LEVEL}", lvl));
        final List<String> l = new ArrayList<>();
        for(String s : m.getLore()) {
            l.add(s.replace("{LEVEL}", lvl).replace("{XP}", XP).replace("{COMPLETION}", xp).replace("{COOLDOWN}", cooldown));
        }
        m.setLore(l);
        is.setItemMeta(m);
        return (ItemStack) createRPItemStack(is, new HashMap<String, String>() {{
            put("InventoryPetInfo", getIdentifier() + ":" + lvl + ":" + XP + ":0");
        }});
    }

    default void setItem(ItemStack is, int level, int exp, long cooldown) {
        final String[] info = getRPItemStackValue(is, "InventoryPetInfo").split(":");
        setRPItemStackValues(is, new HashMap<String, String>() {{
            put("InventoryPetInfo", info[0] + ":" + level + ":" + exp + ":" + cooldown);
        }});
    }

    ItemStack getEgg();
    LinkedHashMap<InventoryPet, Integer> getEggRequiredPets();
}
