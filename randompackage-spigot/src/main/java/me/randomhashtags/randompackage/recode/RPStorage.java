package me.randomhashtags.randompackage.recode;

import me.randomhashtags.randompackage.recode.utils.AbstractBooster;
import me.randomhashtags.randompackage.recode.utils.AbstractLootbox;
import me.randomhashtags.randompackage.recode.utils.AbstractRPFeature;

import java.util.TreeMap;

public abstract class RPStorage {
    private static TreeMap<String, AbstractRPFeature> boosters, lootboxes;

    public AbstractBooster getBooster(String identifier) {
        return boosters != null ? (AbstractBooster) boosters.getOrDefault(identifier, null) : null;
    }
    public void addBooster(String identifier, AbstractBooster b) {
        if(boosters == null) boosters = new TreeMap<>();
        boosters.put(identifier, b);
    }

    public AbstractLootbox getLootbox(String identifier) {
        return lootboxes != null ? (AbstractLootbox) lootboxes.getOrDefault(identifier, null) : null;
    }
    public void addLootbox(String identifier, AbstractLootbox l) {
        if(lootboxes == null) lootboxes = new TreeMap<>();
        lootboxes.put(identifier, l);
    }
}
