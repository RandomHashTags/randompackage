package me.randomhashtags.randompackage.dev.duels;

import me.randomhashtags.randompackage.addon.DuelArena;
import me.randomhashtags.randompackage.enums.Feature;
import me.randomhashtags.randompackage.addon.file.RPAddon;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileDuelArena extends RPAddon implements DuelArena {
    private ItemStack item;
    private List<Location> locations;
    public FileDuelArena(File f) {
        load(f);
        register(Feature.DUEL_ARENA, this);
    }
    public String getIdentifier() { return getYamlName(); }

    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "item");
        return getClone(item);
    }
    public String getName() { return colorize(yml.getString("name")); }
    public List<Location> getLocations() {
        if(locations == null) {
            locations = new ArrayList<>();
            for(String s : yml.getStringList("locations")) {
                locations.add(api.toLocation(s));
            }
        }
        return locations;
    }
}
