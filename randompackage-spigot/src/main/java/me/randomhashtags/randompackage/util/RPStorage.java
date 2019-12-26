package me.randomhashtags.randompackage.util;

import me.randomhashtags.randompackage.addon.*;
import me.randomhashtags.randompackage.dev.Dungeon;
import me.randomhashtags.randompackage.supported.RegionalAPI;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class RPStorage extends RegionalAPI {
    protected static LinkedHashMap<String, EnchantmentOrb> enchantmentorbs;

    private void check(Map<String, ?> map, String identifier, String subject) {
        if(map.containsKey(identifier)) System.out.println("[RandomPackage] Already contains " + subject + " \"" + identifier + "\"");
    }

    /*
        Value Of
     */
    public boolean hasEnchantmentOrb(ItemStack is) {
        if(enchantmentorbs != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final List<String> l = is.getItemMeta().getLore();
            for(EnchantmentOrb orb : enchantmentorbs.values())
                if(l.contains(orb.getApplied()))
                    return true;
        }
        return false;
    }
}