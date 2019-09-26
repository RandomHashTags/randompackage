package me.randomhashtags.randompackage.addon.obj;

import me.randomhashtags.randompackage.addon.FactionUpgradeLevel;

import java.util.List;

public class FactionUpgradeLevelObj implements FactionUpgradeLevel {
    private int asInt;
    private double value;
    private String string;
    private List<String> cost;
    public FactionUpgradeLevelObj(int asInt, double value, String string, List<String> cost) {
        this.asInt = asInt;
        this.value = value;
        this.string = string;
        this.cost = cost;
    }
    public int asInt() { return asInt; }
    public double getValue() { return value; }
    public String getString() { return string; }
    public List<String> getCost() { return cost; }
}
