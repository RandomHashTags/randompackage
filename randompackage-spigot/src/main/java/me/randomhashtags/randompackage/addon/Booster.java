package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Itemable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackageAPI.API;

public interface Booster extends Attributable, Itemable {
    BoosterRecipients getRecipients();
    int getTimeLoreSlot();
    int getMultiplierLoreSlot();
    List<String> getActivateMsg();
    List<String> getExpireMsg();
    List<String> getNotifyMsg();
    default ItemStack getItem(long duration, double multiplier) {
        final String d = API.getRemainingTime(duration), mu = Double.toString(API.round(multiplier, 4));
        final ItemStack i = getItem();
        final ItemMeta m = i.getItemMeta();
        final List<String> l = new ArrayList<>();
        for(String s : m.getLore()) {
            l.add(s.replace("{TIME}", d).replace("{MULTIPLIER}", mu));
        }
        m.setLore(l);
        i.setItemMeta(m);
        return i;
    }
}
