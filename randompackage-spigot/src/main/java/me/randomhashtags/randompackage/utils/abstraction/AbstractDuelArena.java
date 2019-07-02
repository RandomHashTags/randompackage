package me.randomhashtags.randompackage.utils.abstraction;

import me.randomhashtags.randompackage.utils.AbstractRPFeature;
import me.randomhashtags.randompackage.utils.NamespacedKey;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;


public abstract class AbstractDuelArena extends AbstractRPFeature {
    public static HashMap<NamespacedKey, AbstractDuelArena> arenas;

    public void created(NamespacedKey key) {
        if(arenas == null) arenas = new HashMap<>();
        arenas.put(key, this);
    }
    public abstract String getName();
    public abstract List<Location> getLocations();
    public abstract ItemStack getItem();
}
