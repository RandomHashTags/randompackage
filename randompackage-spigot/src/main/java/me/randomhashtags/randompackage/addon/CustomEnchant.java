package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Toggleable;

import java.math.BigDecimal;
import java.util.List;

public interface CustomEnchant extends Attributable, Toggleable {
    String getName();
    List<String> getLore();
    int getMaxLevel();
    List<String> getAppliesTo();
    String getRequiredEnchant();
    BigDecimal[] getAlchemist();
    BigDecimal[] getTinkerer();
    String getEnchantProcValue();

    default BigDecimal getAlchemistUpgradeCost(int level) {
        final BigDecimal[] i = getAlchemist();
        final int l = level-1;
        final BigDecimal d = i[0];
        return l < i.length ? i[l] : BigDecimal.ZERO;
    }
    default BigDecimal getTinkererValue(int level) {
        final BigDecimal[] i = getTinkerer();
        final int l = level-1;
        return l < i.length ? i[l] : BigDecimal.ZERO;
    }
}
