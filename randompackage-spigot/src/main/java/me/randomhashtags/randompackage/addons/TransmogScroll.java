package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Applyable;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class TransmogScroll extends Applyable {
    public abstract List<String> getRarityOrganization();

    public boolean canBeApplied(ItemStack is) {
        if(is != null) {
            final String m = is.getType().name();
            for(String s : getAppliesTo()) {
                if(m.endsWith(s.toUpperCase())) {
                    return true;
                }
            }
        }
        return false;
    }
}
