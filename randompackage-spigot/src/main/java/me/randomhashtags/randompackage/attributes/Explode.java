package me.randomhashtags.randompackage.attributes;

import me.randomhashtags.randompackage.utils.addons.AbstractEventAttribute;
import org.bukkit.Location;

import java.util.HashMap;

public class Explode extends AbstractEventAttribute {
    @Override
    public void executeAt(HashMap<Location, String> locations) {
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
