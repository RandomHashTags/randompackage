package me.randomhashtags.randompackage.utils.classes;

import me.randomhashtags.randompackage.utils.abstraction.AbstractFactionUpgrade;

import java.io.File;
import java.util.TreeMap;

public class FactionUpgrade extends AbstractFactionUpgrade {
    public static TreeMap<String, FactionUpgrade> upgrades;

    public FactionUpgrade(File f) {
        if(upgrades == null) upgrades = new TreeMap<>();
        load(f);
        upgrades.put(getYamlName(), this);
    }

    public void setPerks(String faction) {

    }

    public static FactionUpgrade valueOf(int slot) {
        if(upgrades != null) {
            for(FactionUpgrade f : upgrades.values()) {
                if(f.getSlot() == slot) {
                    return f;
                }
            }
        }
        return null;
    }

    public static void deleteAll() {
        upgrades = null;
    }
}
