package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.usingpath.PathRarityGem;
import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public abstract class SoulTracker extends Itemable {
    public abstract String getTracks();
    public abstract String[] getAppliesTo();
    public abstract String getSoulsPerKill();
    public abstract double getSoulsCollected();
    public abstract RarityGem getConvertsTo();
    public abstract String getAppliedLore();
    public abstract List<String> getApplyMsg();
    public abstract List<String> getSplitMsg();

    public static SoulTracker valueOf(PathRarityGem gem) {
        if(soultrackers != null)
            for(SoulTracker st : soultrackers.values())
                if(st.getConvertsTo().equals(gem))
                    return st;
        return null;
    }
    public static SoulTracker valueOf(ItemStack is) {
        if(soultrackers != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final ItemMeta m = is.getItemMeta();
            for(SoulTracker s : soultrackers.values()) {
                if(s.getItem().getItemMeta().equals(m)) {
                    return s;
                }
            }
        }
        return null;
    }
    public static HashMap<Integer, SoulTracker> valueOfApplied(ItemStack is) {
        if(soultrackers != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final List<String> l = is.getItemMeta().getLore();
            final Collection<SoulTracker> trackers = soultrackers.values();
            int slot = 0;
            for(String s : l) {
                for(SoulTracker t : trackers) {
                    final String a = t.getAppliedLore().replace("{SOULS}", "");
                    if(s.startsWith(a)) {
                        final HashMap<Integer, SoulTracker> h = new HashMap<>();
                        h.put(slot, t);
                        return h;
                    }
                }
                slot++;
            }
        }
        return null;
    }
    public static SoulTracker valueOf(String appliedlore) {
        if(soultrackers != null) {
            for(SoulTracker st : soultrackers.values()) {
                if(appliedlore.startsWith(st.getAppliedLore().replace("{SOULS}", ""))) {
                    return st;
                }
            }
        }
        return null;
    }
}
