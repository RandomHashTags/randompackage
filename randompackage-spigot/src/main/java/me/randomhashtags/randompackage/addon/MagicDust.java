package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.AppliesToRarities;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.Percentable;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

public interface MagicDust extends AppliesToRarities, Itemable, Percentable, GivedpItemable {

    default String[] getGivedpItemIdentifiers() {
        return new String[] { "dust" };
    }
    default ItemStack valueOfInput(String originalInput, String lowercaseInput) {
        final String[] values = originalInput.split(":");
        final MagicDust d = getMagicDust(values[1]);
        final int percent = values.length >= 3 ? Integer.parseInt(values[2]) : -1;
        final ItemStack target = d != null ? percent == -1 ? d.getRandomPercentItem(RANDOM) : d.getItem(percent) : null;
        return target != null ? target : AIR;
    }

    int getChance();
    MagicDust getUpgradesTo();
    BigDecimal getUpgradeCost();
}
