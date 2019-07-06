package me.randomhashtags.randompackage.addons.usingfile;

import me.randomhashtags.randompackage.addons.DuelArena;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileDuelArena extends DuelArena {
    private ItemStack item;
    public FileDuelArena(File f) {
        load(f);
        addDuelArena(getIdentifier(), this);
    }
    public String getIdentifier() { return getYamlName(); }

    public ItemStack getItem() {
        if(item == null) item = api.d(yml, "item");
        return item.clone();
    }
    public String getName() { return ChatColor.translateAlternateColorCodes('&', yml.getString("name")); }
    public List<Location> getLocations() {
        final List<Location> l = new ArrayList<>();
        for(String s : yml.getStringList("locations")) l.add(api.toLocation(s));
        return l;
    }

}
