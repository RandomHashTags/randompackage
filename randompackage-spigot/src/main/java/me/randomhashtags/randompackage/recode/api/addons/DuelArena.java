package me.randomhashtags.randompackage.recode.api.addons;

import me.randomhashtags.randompackage.recode.RPAddon;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class DuelArena extends RPAddon {
    public abstract String getName();
    public abstract List<Location> getLocations();
    public abstract ItemStack getItem();
}
