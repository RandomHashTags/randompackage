package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.Itemable;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;

public interface Booster extends Itemable {
    String getRecipients();
    int getTimeLoreSlot();
    int getMultiplierLoreSlot();
    List<String> getActivateMsg();
    List<String> getExpireMsg();
    List<String> getNotifyMsg();
    List<String> getAttributes();
    ItemStack getItem(long duration, double multiplier);
}
