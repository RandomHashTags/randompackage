package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Applyable;
import me.randomhashtags.randompackage.addon.util.Percentable;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface EnchantmentOrb extends Applyable, Percentable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "enchantmentorb" };
    }
    default ItemStack valueOfInput(String originalInput, String lowercaseInput) {
        final String[] values = originalInput.split(":");
        String path = values[1], percent = values.length == 3 ? values[2] : Integer.toString(RANDOM.nextInt(101));
        EnchantmentOrb orb = getEnchantmentOrb(path);
        if(orb == null) {
            final List<EnchantmentOrb> list = new ArrayList<>();
            for(String s : getAllEnchantmentOrbs().keySet()) {
                if(s.startsWith(path)) {
                    list.add(getEnchantmentOrb(s));
                }
            }
            orb = !list.isEmpty() ? list.get(RANDOM.nextInt(list.size())) : null;
        }
        final boolean hasHyphen = percent.contains("-");
        final int min = hasHyphen ? Integer.parseInt(percent.split("-")[0]) : Integer.parseInt(percent), P = hasHyphen ? min+RANDOM.nextInt(Integer.parseInt(percent.split("-")[1])-min+1) : min;
        return orb != null ? orb.getItem(P) : AIR;
    }

    int getMaxAllowableEnchants();
    int getPercentLoreSlot();
    int getIncrement();
}
