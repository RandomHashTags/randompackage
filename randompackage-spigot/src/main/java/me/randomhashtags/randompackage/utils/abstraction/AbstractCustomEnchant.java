package me.randomhashtags.randompackage.utils.abstraction;

import me.randomhashtags.randompackage.utils.AbstractRPFeature;
import me.randomhashtags.randompackage.utils.universal.UMaterial;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;
import java.util.TreeMap;

public abstract class AbstractCustomEnchant extends AbstractRPFeature {
    public static TreeMap<String, AbstractCustomEnchant> enabled, disabled;

    @Override
    public void load(File f) {
        super.load(f);
        if(enabled == null) {
            enabled = new TreeMap<>();
            disabled = new TreeMap<>();
        }
        (isEnabled() ? enabled : disabled).put(getYamlName(), this);
    }
    public abstract boolean isEnabled();
    public abstract String getName();
    public abstract List<String> getLore();
    public abstract int getMaxLevel();
    public abstract List<String> getAppliesTo();
    public abstract String getRequiredEnchant();
    public abstract int[] getAlchemist();
    public abstract int getAlchemistUpgradeCost(int level);
    public abstract int[] getTinkerer();
    public abstract int getTinkererValue(int level);
    public abstract String getEnchantProcValue();
    public abstract List<String> getAttributes();

    public static AbstractCustomEnchant valueOf(String string) { return valueOf(string, false); }
    public static AbstractCustomEnchant valueOf(String string, boolean checkDisabledEnchants) {
        if(disabled != null && enabled != null && string != null) {
            final String s = ChatColor.stripColor(string);
            for(AbstractCustomEnchant ce : enabled.values()) {
                if(s.startsWith(ChatColor.stripColor(ce.getName())))
                    return ce;
            }
            if(checkDisabledEnchants) {
                for(AbstractCustomEnchant ce : disabled.values()) {
                    if(s.startsWith(ChatColor.stripColor(ce.getName())))
                        return ce;
                }
            }
        }
        return null;
    }
    public static AbstractCustomEnchant valueOf(ItemStack is) {
        if(is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final AbstractCustomEnchant e = valueOf(is.getItemMeta().getDisplayName());
            final AbstractEnchantRarity r = AbstractEnchantRarity.valueOf(e);
            return e != null && UMaterial.match(is).equals(UMaterial.match(r.getRevealedItem())) ? e : null;
        }
        return null;
    }
    public static void deleteAll() {
        enabled = null;
        disabled = null;
    }
}
