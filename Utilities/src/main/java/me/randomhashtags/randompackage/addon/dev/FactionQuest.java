package me.randomhashtags.randompackage.addon.dev;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Nameable;
import me.randomhashtags.randompackage.addon.util.Rewardable;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;

public interface FactionQuest extends Nameable, Attributable, Rewardable {
    @NotNull BigDecimal getCompletion();
    @NotNull List<String> getLore();
}
