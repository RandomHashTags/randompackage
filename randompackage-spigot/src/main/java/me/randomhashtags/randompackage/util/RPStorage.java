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
    protected static TreeMap<String, CustomEnchant> enabled, disabled;

    protected static LinkedHashMap<String, Dungeon> dungeons;
    protected static LinkedHashMap<String, EnchantmentOrb> enchantmentorbs;
    protected static LinkedHashMap<String, CustomKit> kits;
    protected static LinkedHashMap<String, Outpost> outposts;
    protected static LinkedHashMap<String, InventoryPet> inventorypets;

    private void check(Map<String, ?> map, String identifier, String subject) {
        if(map.containsKey(identifier)) System.out.println("[RandomPackage] Already contains " + subject + " \"" + identifier + "\"");
    }

    public CustomEnchant getEnchant(String identifier) {
        return enabled != null && enabled.containsKey(identifier) ? enabled.get(identifier) : disabled != null && disabled.containsKey(identifier) ? disabled.get(identifier) : null;
    }
    public void addEnchant(CustomEnchant enchant) {
        final boolean e = enchant.isEnabled();
        if(e && enabled == null) enabled = new TreeMap<>();
        else if(!e && disabled == null) disabled = new TreeMap<>();
        final String identifier = enchant.getIdentifier();
        check(e ? enabled : disabled, identifier, "custom enchant");
        (e ? enabled : disabled).put(identifier, enchant);
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

    public CustomKit valueOfFallenHeroSpawnItem(ItemStack is, Class type) {
        if(is != null && kits != null) {
            for(CustomKit k : kits.values()) {
                final ItemStack f = k.getFallenHeroItem(k, true);
                if(f != null && (type == null || k.getClass().isInstance(type)) && f.isSimilar(is)) {
                    return k;
                }
            }
        }
        return null;
    }
    public CustomKit valueOfFallenHeroGem(ItemStack is, Class type) {
        if(is != null && kits != null) {
            for(CustomKit k : kits.values()) {
                final ItemStack f = k.getFallenHeroItem(k, false);
                if(f != null && (type == null || k.getClass().isInstance(type)) && f.isSimilar(is)) {
                    return k;
                }
            }
        }
        return null;
    }

    public InventoryPet valueOfInventoryPet(ItemStack is) {
        if(inventorypets != null && is != null) {
            for(InventoryPet p : inventorypets.values()) {
                final ItemStack i = p.getItem();
            }
        }
        return null;
    }

}