package me.randomhashtags.randompackage.dev.duels;

import me.randomhashtags.randompackage.addon.DuelArena;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.addon.file.RPAddonSpigot;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileDuelArena extends RPAddonSpigot implements DuelArena {
    private ItemStack item;
    private List<Location> locations;

    public FileDuelArena(File f) {
        load(f);
        register(Feature.DUEL_ARENA, this);
    }

    @NotNull
    public ItemStack getItem() {
        if(item == null) item = createItemStack(yml, "item");
        return getClone(item);
    }
    public String getName() { return colorize(yml.getString("name")); }
    public List<Location> getLocations() {
        if(locations == null) {
            locations = new ArrayList<>();
            for(String s : yml.getStringList("locations")) {
                locations.add(toLocation(s));
            }
        }
        return locations;
    }
}
