package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.enums.BoosterRecipients;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface Booster extends Attributable, Itemable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "booster" };
    }
    @Nullable
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        final String[] values = originalInput.split(":");
        final Booster booster = getBooster(values[1]);
        return booster != null ? booster.getItem(Long.parseLong(values[3])*1000, Double.parseDouble(values[2])) : null;
    }

    @NotNull BoosterRecipients getRecipients();
    int getTimeLoreSlot();
    int getMultiplierLoreSlot();
    @NotNull List<String> getActivateMsg();
    @NotNull List<String> getExpireMsg();
    @NotNull List<String> getNotifyMsg();
    @NotNull
    default ItemStack getItem(long duration, double multiplier) {
        final String durationString = getRemainingTime(duration), multiplierString = Double.toString(round(multiplier, 4));
        final ItemStack is = getItem();
        final ItemMeta m = is.getItemMeta();
        final List<String> l = new ArrayList<>();
        for(String s : m.getLore()) {
            l.add(s.replace("{TIME}", durationString).replace("{MULTIPLIER}", multiplierString));
        }
        m.setLore(l);
        is.setItemMeta(m);
        return is;
    }
}
