package me.randomhashtags.randompackage.utils.abstraction;

import org.bukkit.inventory.ItemStack;

import java.util.TreeMap;

public class AbstractPet extends Saveable {
    private long cooldown;
    private ItemStack item;
    private TreeMap<Integer, Long> xpRequiredForLevel;
}
