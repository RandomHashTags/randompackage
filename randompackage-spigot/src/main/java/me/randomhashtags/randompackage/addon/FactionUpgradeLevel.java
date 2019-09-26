package me.randomhashtags.randompackage.addon;

import java.util.List;

public interface FactionUpgradeLevel {
    int asInt();
    double getValue();
    String getString();
    List<String> getCost();
}
