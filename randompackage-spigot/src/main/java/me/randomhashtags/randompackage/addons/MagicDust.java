package me.randomhashtags.randompackage.addons;

import me.randomhashtags.randompackage.addons.utils.AppliesToRarities;
import me.randomhashtags.randompackage.addons.utils.Itemable;
import me.randomhashtags.randompackage.addons.utils.Percentable;

import java.math.BigDecimal;

public interface MagicDust extends AppliesToRarities, Itemable, Percentable {
    int getChance();
    MagicDust getUpgradesTo();
    BigDecimal getUpgradeCost();
}
