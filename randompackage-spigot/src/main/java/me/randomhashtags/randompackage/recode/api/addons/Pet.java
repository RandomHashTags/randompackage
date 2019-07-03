package me.randomhashtags.randompackage.recode.api.addons;

import me.randomhashtags.randompackage.recode.RPAddon;
import org.bukkit.inventory.ItemStack;

import java.util.TreeMap;

public abstract class Pet extends RPAddon {
    public abstract TreeMap<Integer, Long> getCooldownForLevel();
    public abstract ItemStack getItem();
    public abstract TreeMap<Integer, Long> getRequiredXpForLevel();
}
