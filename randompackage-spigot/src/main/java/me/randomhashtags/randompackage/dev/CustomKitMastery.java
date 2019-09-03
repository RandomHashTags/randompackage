package me.randomhashtags.randompackage.dev;

import me.randomhashtags.randompackage.addons.CustomKit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.LinkedHashMap;
import java.util.List;

public interface CustomKitMastery extends CustomKit {
    String getName();
    ItemStack getRedeem();
    LinkedHashMap<CustomKit, Integer> getRequiredKits();
    boolean losesRequiredKits();
    ItemStack getShard();
    ItemStack getAntiCrystal();
    default ItemStack getAntiCrystal(int percent) {
        final ItemStack i = getAntiCrystal();
        final ItemMeta m = i.getItemMeta();
        final List<String> l = m.getLore();
        for(String s : m.getLore()) l.add(s.replace("{PERCENT}", Integer.toString(percent)));
        m.setLore(l);
        i.setItemMeta(m);
        return i;
    }
    List<String> getAntiCrystalNegatedEnchants();
    String getAntiCrystalApplied();
}
