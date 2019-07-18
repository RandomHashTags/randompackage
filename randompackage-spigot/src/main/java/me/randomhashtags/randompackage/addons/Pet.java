package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.TreeMap;

public abstract class Pet extends Itemable {

    public abstract TreeMap<Integer, Long> getCooldowns();
    public abstract TreeMap<Integer, Long> getRequiredXp();

    public int getCooldownSlot() { return get("{COOLDOWN}"); }
    public int getExpSlot() { return get("{EXP}"); }
    private int get(String input) {
        final List<String> l = getItem().getItemMeta().getLore();
        for(int i = 0; i < l.size(); i++) {
            if(l.get(i).contains(input)) {
                return i;
            }
        }
        return -1;
    }

    public static Pet valueOf(ItemStack is) {
        if(pets != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            for(Pet p : pets.values()) {
                final ItemStack i = p.getItem();
            }
        }
        return null;
    }
}
