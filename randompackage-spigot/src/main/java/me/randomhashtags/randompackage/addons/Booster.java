package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import me.randomhashtags.randompackage.utils.objects.TObject;
import me.randomhashtags.randompackage.utils.universal.UVersion;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public abstract class Booster extends Itemable {
    public abstract String getRecipients();
    public abstract int getTimeLoreSlot();
    public abstract int getMultiplierLoreSlot();
    public abstract List<String> getActivateMsg();
    public abstract List<String> getExpireMsg();
    public abstract List<String> getNotifyMsg();
    public abstract List<String> getAttributes();

    public ItemStack getItem(long duration, double multiplier) {
        final String d = getRemainingTime(duration), mu = Double.toString(api.round(multiplier, 4));
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

    public static TObject valueOf(ItemStack is) {
        if(boosters != null && is != null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && is.getItemMeta().hasLore()) {
            final ItemMeta m = is.getItemMeta();
            final String d = m.getDisplayName();
            final List<String> l = m.getLore();
            final UVersion u = getUVersion();
            for(Booster b : boosters.values()) {
                final ItemStack i = b.getItem();
                if(d.equals(i.getItemMeta().getDisplayName())) {
                    double multiplier = u.getRemainingDouble(ChatColor.stripColor(l.get(b.getMultiplierLoreSlot())));
                    long duration = u.getTime(ChatColor.stripColor(l.get(b.getTimeLoreSlot())));
                    return new TObject(b, multiplier, duration);
                }
            }
        }
        return null;
    }
}
