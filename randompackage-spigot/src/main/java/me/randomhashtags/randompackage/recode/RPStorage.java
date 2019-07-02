package me.randomhashtags.randompackage.recode;

import me.randomhashtags.randompackage.recode.utils.AbstractBooster;
import me.randomhashtags.randompackage.recode.utils.AbstractLootbox;

import java.util.TreeMap;

public class RPStorage {

    private TreeMap<String, AbstractBooster> boosters = new TreeMap<>();
    private TreeMap<String, AbstractLootbox> lootboxes = new TreeMap<>();

    public AbstractLootbox getLootbox(String identifier) { return lootboxes.getOrDefault(identifier, null); }
    public void addLootbox(String identifier, AbstractLootbox l) { lootboxes.put(identifier, l); }
}
