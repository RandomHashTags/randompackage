package me.randomhashtags.randompackage.utils.abstraction;

import me.randomhashtags.randompackage.utils.AbstractRPFeature;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractEnchantRarity extends AbstractRPFeature {
    public static HashMap<String, AbstractEnchantRarity> rarities;

    public abstract YamlConfiguration getSettingsYaml();
    public abstract String getName();
    public abstract String[] getRevealedEnchantRarities();
    public abstract List<String> getRevealedEnchantMsg();
    public abstract ItemStack getRevealItem();
    public abstract ItemStack getRevealedItem();
    public abstract String getNameColors();
    public abstract String getApplyColors();
    public abstract boolean percentsAddUpto100();
    public abstract String getSuccess();
    public abstract String getDestroy();
    public abstract List<String> getLoreFormat();
    public abstract int getSuccessSlot();
    public abstract int getDestroySlot();
    public abstract Firework getFirework();
    public abstract List<AbstractCustomEnchant> getEnchants();

    public static AbstractEnchantRarity valueOf(ItemStack is) {
        if(is != null && rarities != null) {
            for(AbstractEnchantRarity r : rarities.values()) {
                final ItemStack re = r.getRevealItem();
                if(re != null && re.isSimilar(is)) {
                    return r;
                }
            }
        }
        return null;
    }
    public static AbstractEnchantRarity valueOf(AbstractCustomEnchant enchant) {
        if(rarities != null) {
            for(AbstractEnchantRarity e : rarities.values()) {
                if(e.getEnchants().contains(enchant)) {
                    return e;
                }
            }
        }
        return null;
    }
    public static void deleteAll() {
        rarities = null;
    }
}
