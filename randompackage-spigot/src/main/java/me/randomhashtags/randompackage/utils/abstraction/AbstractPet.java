package me.randomhashtags.randompackage.utils.abstraction;

import me.randomhashtags.randompackage.utils.AbstractRPFeature;
import org.bukkit.inventory.ItemStack;

import java.util.TreeMap;

public abstract class AbstractPet extends AbstractRPFeature {
    private long cooldown;
    private ItemStack item;
    private TreeMap<Integer, Long> xpRequiredForLevel;
}
