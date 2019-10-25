package me.randomhashtags.randompackage.dev.duels;

import me.randomhashtags.randompackage.dev.duels.DuelArena;
import me.randomhashtags.randompackage.util.addon.RPAddon;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileDuelArena extends RPAddon implements DuelArena {
    private ItemStack item;
    public FileDuelArena(File f) {
        load(f);
        addDuelArena(this);
    }
    public String getIdentifier() { return getYamlName(); }

    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "item");
        return getClone(item);
    }
    public String getName() { return ChatColor.translateAlternateColorCodes('&', yml.getString("name")); }
    public List<Location> getLocations() {
        final List<Location> l = new ArrayList<>();
        for(String s : yml.getStringList("locations")) l.add(api.toLocation(s));
        return l;
    }

}
