package me.randomhashtags.randompackage.utils.classes.customenchants;

import me.randomhashtags.randompackage.utils.abstraction.AbstractCustomEnchant;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.TreeMap;

public class CustomEnchant extends AbstractCustomEnchant {
    public static TreeMap<String, CustomEnchant> enabled, disabled;

    public CustomEnchant(File f) {
        if(enabled == null) {
            enabled = new TreeMap<>();
            disabled = new TreeMap<>();
        }
        load(f);
        (isEnabled() ? enabled : disabled).put(getYamlName(), this);
    }

    public static CustomEnchant valueOf(String string) { return valueOf(string, false); }
    public static CustomEnchant valueOf(String string, boolean checkDisabledEnchants) {
        if(disabled != null && enabled != null && string != null) {
            final String s = ChatColor.stripColor(string);
            for(CustomEnchant ce : enabled.values()) {
                if(s.startsWith(ChatColor.stripColor(ce.getName())))
                    return ce;
            }
            if(checkDisabledEnchants) {
                for(CustomEnchant ce : disabled.values()) {
                    if(s.startsWith(ChatColor.stripColor(ce.getName())))
                        return ce;
                }
            }
        }
        return null;
    }
    public static CustomEnchant valueOf(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final CustomEnchant e = valueOf(is.getItemMeta().getDisplayName());
            final EnchantRarity r = EnchantRarity.valueOf(e);
            return e != null && UMaterial.match(is).equals(UMaterial.match(r.getRevealedItem())) ? e : null;
        }
        return null;
    }
    public static void deleteAll() {
        enabled = null;
        disabled = null;
    }
}
