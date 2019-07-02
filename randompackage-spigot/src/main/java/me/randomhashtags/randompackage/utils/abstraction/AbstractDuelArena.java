package me.randomhashtags.randompackage.utils.abstraction;

import me.randomhashtags.randompackage.utils.AbstractRPFeature;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;


public abstract class AbstractDuelArena extends AbstractRPFeature {
    public static HashMap<String, AbstractDuelArena> arenas;

    public void created(String identifier) {
        if(arenas == null) arenas = new HashMap<>();
        arenas.put(identifier, this);
    }
    public abstract String getName();
    public abstract List<Location> getLocations();
    public abstract ItemStack getItem();
}
