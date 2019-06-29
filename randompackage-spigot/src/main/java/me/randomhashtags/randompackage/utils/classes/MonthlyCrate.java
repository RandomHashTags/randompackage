package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.utils.abstraction.AbstractMonthlyCrate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MonthlyCrate extends AbstractMonthlyCrate {
    public static HashMap<String, MonthlyCrate> crates;
    public static HashMap<Integer, HashMap<Integer, MonthlyCrate>> categorySlots;

    public MonthlyCrate(File f) {
        if(crates == null) {
            crates = new HashMap<>();
            revealedRegular = new HashMap<>();
            revealedBonus = new HashMap<>();
            categorySlots = new HashMap<>();
        }
        load(f);
        final int category = getCategory(), categorySlot = getCategorySlot();
        if(!categorySlots.containsKey(category)) {
            final HashMap<Integer, MonthlyCrate> C = new HashMap<>();
            C.put(categorySlot, this);
            categorySlots.put(category, C);
        } else {
            categorySlots.get(category).put(categorySlot, this);
        }
        crates.put(getYamlName(), this);
    }

    public static void deleteAll() {
        crates = null;
        revealedRegular = null;
        revealedBonus = null;
        random = null;
    }

    public static MonthlyCrate valueOf(String title) {
        if(crates != null) {
            for(MonthlyCrate m : crates.values()) {
                if(m.getGuiTitle().equals(title)) {
                    return m;
                }
            }
        }
        return null;
    }
    public static MonthlyCrate valueOf(ItemStack item) {
        if(crates != null) {
            for(MonthlyCrate c : crates.values()) {
                if(c.getItem().isSimilar(item)) {
                    return c;
                }
            }
        }
        return null;
    }
    public static MonthlyCrate valueOf(Player player, ItemStack item) {
        if(crates != null && player != null && item != null) {
            final String p = player.getName();
            for(MonthlyCrate c : crates.values()) {
                final ItemStack is = c.getItem(), IS = is.clone();
                final ItemMeta m = is.getItemMeta();
                final List<String> s = new ArrayList<>();
                if(m.hasLore()) {
                    for(String l : m.getLore()) {
                        s.add(l.replace("{UNLOCKED_BY}", p));
                    }
                    m.setLore(s);
                }
                is.setItemMeta(m);
                if(item.isSimilar(is) || item.isSimilar(IS)) {
                    return c;
                }
            }
        }
        return null;
    }
    public static MonthlyCrate valueOf(int category, int slot) {
        return categorySlots != null && categorySlots.containsKey(category) ? categorySlots.get(category).getOrDefault(slot, null) : null;
    }
}