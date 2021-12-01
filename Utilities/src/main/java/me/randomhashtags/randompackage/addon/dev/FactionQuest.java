package me.randomhashtags.randompackage.addon.dev;

import me.randomhashtags.randompackage.addon.util.Attributable;
import me.randomhashtags.randompackage.addon.util.Nameable;
import me.randomhashtags.randompackage.addon.util.Rewardable;

import java.math.BigDecimal;
import java.util.List;

public interface FactionQuest extends Nameable, Attributable, Rewardable {
    BigDecimal getCompletion();
    List<String> getLore();
}
