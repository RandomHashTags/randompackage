package me.randomhashtags.randompackage.utils.abstraction;


import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class AbstractDuelArena extends Saveable {

    private ItemStack item;

    public String getName() { return ChatColor.translateAlternateColorCodes('&', yml.getString("name")); }
    public List<Location> getLocations() {
        final List<Location> l = new ArrayList<>();
        for(String s : yml.getStringList("locations")) l.add(api.toLocation(s));
        return l;
    }
    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "item");
        return item.clone();
    }
}
