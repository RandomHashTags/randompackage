package me.randomhashtags.randompackage.attribute;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public final class Explode extends AbstractEventAttribute {
    @Override
    public void executeAt(@NotNull HashMap<Location, String> locations) {
        for(Location l : locations.keySet()) {
            final String v = locations.get(l);
            if(v != null) {
                final String[] s = v.split(":");
                final int size = s.length;
                l.getWorld().createExplosion(l.getX(), l.getY(), l.getZ(), (float) evaluate(s[1]), size >= 3 && Boolean.parseBoolean(s[2]), size >= 4 && Boolean.parseBoolean(s[3]));
            }
        }
    }
}
