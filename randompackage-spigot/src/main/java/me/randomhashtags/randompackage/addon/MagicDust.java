package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.AppliesToRarities;
import me.randomhashtags.randompackage.addon.util.Itemable;
import me.randomhashtags.randompackage.addon.util.Percentable;

import java.math.BigDecimal;

public interface MagicDust extends AppliesToRarities, Itemable, Percentable {
    int getChance();
    MagicDust getUpgradesTo();
    BigDecimal getUpgradeCost();
}
