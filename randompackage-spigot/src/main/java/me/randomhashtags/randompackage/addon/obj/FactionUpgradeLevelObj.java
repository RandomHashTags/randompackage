package me.randomhashtags.randompackage.addon.obj;

import me.randomhashtags.randompackage.addon.FactionUpgradeLevel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class FactionUpgradeLevelObj implements FactionUpgradeLevel {
    private final int asInt;
    private final double value;
    private final String string;
    private final List<String> cost;
    public FactionUpgradeLevelObj(int asInt, double value, String string, @NotNull List<String> cost) {
        this.asInt = asInt;
        this.value = value;
        this.string = string;
        this.cost = cost;
    }
    public int asInt() {
        return asInt;
    }
    public double getValue() {
        return value;
    }
    public String getString() {
        return string;
    }
    public @NotNull List<String> getCost() {
        return cost;
    }
}
