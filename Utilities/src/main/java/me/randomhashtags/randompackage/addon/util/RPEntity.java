package me.randomhashtags.randompackage.addon.util;

import org.jetbrains.annotations.NotNull;

public interface RPEntity extends Attributable, Nameable, Rewardable {
    @NotNull String getType();
}
