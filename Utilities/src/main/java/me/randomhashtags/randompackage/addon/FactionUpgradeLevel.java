package me.randomhashtags.randompackage.addon;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FactionUpgradeLevel {
    int asInt();
    double getValue();
    String getString();
    @NotNull List<String> getCost();
}
