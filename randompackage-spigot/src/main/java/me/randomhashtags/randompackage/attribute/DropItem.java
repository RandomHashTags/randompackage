package me.randomhashtags.randompackage.attribute;

import me.randomhashtags.randompackage.RandomPackageAPI;
import me.randomhashtags.randompackage.attributesys.PendingEventAttribute;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class DropItem extends AbstractEventAttribute {
    @Override
    public void execute(@NotNull PendingEventAttribute pending) {
        final HashMap<Entity, String> recipientValues = pending.getRecipientValues();
        for(Entity e : recipientValues.keySet()) {
            dropitem(e, recipientValues.get(e).replace("entity", e.getName()));
        }
    }
    @Override
    public void executeAt(@NotNull HashMap<Location, String> locations) {
        for(Location l : locations.keySet()) {
            dropitem(l, locations.get(l));
        }
    }

    private void dropitem(Object o, String value) {
        final boolean isEntity = o instanceof Entity;
        final Entity e = isEntity ? (Entity) o : null;
        final Location l = isEntity ? e.getLocation() : (Location) o;

        final ItemStack is = RandomPackageAPI.INSTANCE.createItemStack(null, value);
        if(is != null && !is.getType().equals(Material.AIR)) {
            l.getWorld().dropItem(l, is);
        }
    }
}
