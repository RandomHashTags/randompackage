package me.randomhashtags.randompackage.attributes;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

import static me.randomhashtags.randompackage.RandomPackageAPI.api;

public class DropItem extends AbstractEventAttribute {
    @Override
    public void execute(HashMap<Entity, String> recipientValues) {
        for(Entity e : recipientValues.keySet()) {
            dropitem(e, recipientValues.get(e).replace("entity", e.getName()));
        }
    }
    @Override
    public void executeAt(HashMap<Location, String> locations) {
        for(Location l : locations.keySet()) {
            dropitem(l, locations.get(l));
        }
    }

    private void dropitem(Object o, String value) {
        final boolean isEntity = o instanceof Entity;
        final Entity e = isEntity ? (Entity) o : null;
        final Location l = isEntity ? e.getLocation() : (Location) o;

        final ItemStack is = api.d(null, value);
        if(is != null && !is.getType().equals(Material.AIR)) {
            l.getWorld().dropItem(l, is);
        }
    }
}
