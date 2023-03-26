package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.AppliesToRarities;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.Percentable;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;

public interface MagicDust extends AppliesToRarities, Itemable, Percentable, GivedpItemableSpigot {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "dust" };
    }
    default ItemStack valueOfInput(@NotNull String originalInput, @NotNull String lowercaseInput) {
        final String[] values = originalInput.split(":");
        final MagicDust dust = getMagicDust(values[1]);
        final int percent = values.length >= 3 ? Integer.parseInt(values[2]) : -1;
        return dust != null ? percent == -1 ? dust.getRandomPercentItem(RANDOM) : dust.getItem(percent) : null;
    }

    int getChance();
    MagicDust getUpgradesTo();
    BigDecimal getUpgradeCost();
}
