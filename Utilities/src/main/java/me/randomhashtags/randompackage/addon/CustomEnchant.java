package me.randomhashtags.randompackage.addon;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.MaxLevelable;
import me.randomhashtags.randompackage.addon.util.Nameable;
import me.randomhashtags.randompackage.addon.util.Toggleable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;

public interface CustomEnchant extends Attributable, MaxLevelable, Nameable, Toggleable {

    String[] getGivedpItemIdentifiers();

    @NotNull
    List<String> getEnabledInWorlds();
    @NotNull
    List<String> getLore();
    @NotNull
    List<String> getAppliesTo();
    @Nullable
    String getRequiredEnchant();
    @Nullable
    List<BigDecimal> getAlchemist();
    @Nullable
    List<BigDecimal> getTinkerer();
    String getEnchantProcValue();

    @NotNull
    default BigDecimal getAlchemistUpgradeCost(int level) {
        final List<BigDecimal> values = getAlchemist();
        if(values != null) {
            final int index = level - 1;
            return index < values.size() ? values.get(index) : BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }
    @NotNull
    default BigDecimal getTinkererValue(int level) {
        final List<BigDecimal> values = getTinkerer();
        if(values != null) {
            final int index = level - 1;
            return index < values.size() ? values.get(index) : BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }
}
