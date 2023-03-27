package me.randomhashtags.randompackage.addon.util;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface Rewardable extends Identifiable {
    @NotNull List<String> getRewards();
}
