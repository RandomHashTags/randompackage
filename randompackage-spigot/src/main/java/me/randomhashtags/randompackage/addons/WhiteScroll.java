package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Applyable;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class WhiteScroll extends Applyable {
    public abstract String getRequiredWhiteScroll();
    public abstract boolean removesRequiredAfterApplication();

    public boolean canBeApplied(ItemStack is) {
        if(is != null && !is.getType().name().contains("AIR")) {
            final List<WhiteScroll> a = valueOfApplied(is);
            final String reqws = getRequiredWhiteScroll();
            if(a == null && reqws == null || a != null && !a.contains(this) && (reqws == null || a.contains(getWhiteScroll(reqws)))) {
                final String m = is.getType().name();
                for(String s : getAppliesTo()) {
                    if(m.endsWith(s.toUpperCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static WhiteScroll valueOf(String apply) {
        if(whitescrolls != null && apply != null && !apply.isEmpty()) {
            for(WhiteScroll w : whitescrolls.values()) {
                if(w.getApplied().equals(apply)) {
                    return w;
                }
            }
        }
        return null;
    }
    public static WhiteScroll valueOf(ItemStack is) {
        if(whitescrolls != null && is != null) {
            for(WhiteScroll w : whitescrolls.values()) {
                if(is.isSimilar(w.getItem())) {
                    return w;
                }
            }
        }
        return null;
    }
    public static List<WhiteScroll> valueOfApplied(ItemStack is) {
        if(whitescrolls != null && is != null && is.hasItemMeta() && is.getItemMeta().hasLore()) {
            final List<WhiteScroll> l = new ArrayList<>();
            for(String s : is.getItemMeta().getLore()) {
                final WhiteScroll w = valueOf(s);
                if(w != null) l.add(w);
            }
            return l;
        }
        return null;
    }
}
