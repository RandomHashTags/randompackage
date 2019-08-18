package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Attributable;
import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Booster extends Attributable, Itemable {
    String getRecipients();
    int getTimeLoreSlot();
    int getMultiplierLoreSlot();
    List<String> getActivateMsg();
    List<String> getExpireMsg();
    List<String> getNotifyMsg();
    ItemStack getItem(long duration, double multiplier);
}
